package ir.maktabsharif.service.impl;

import ir.maktabsharif.model.BaseUser;
import ir.maktabsharif.model.Email;
import ir.maktabsharif.model.EmailConfirmationToken;
import ir.maktabsharif.model.enumeration.TradesManStatus;
import ir.maktabsharif.model.enumeration.UserRole;
import ir.maktabsharif.repository.UserRepository;
import ir.maktabsharif.service.EmailConfirmationTokenService;
import ir.maktabsharif.service.EmailService;
import ir.maktabsharif.service.UserService;
import ir.maktabsharif.service.dto.request.UserChangePasswordDTO;
import ir.maktabsharif.service.dto.request.UserEditProfileDTO;
import ir.maktabsharif.util.Policy;
import ir.maktabsharif.util.SemaphoreUtil;
import ir.maktabsharif.util.Validation;
import ir.maktabsharif.util.exception.InvalidInputException;
import jakarta.mail.MessagingException;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@Primary
public class UserServiceImpl implements UserService {
    protected final UserRepository userRepository;
    private final EmailConfirmationTokenService emailConfirmationTokenService;
    private final EmailService emailService;
    protected final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository, EmailConfirmationTokenService emailConfirmationTokenService, EmailService emailService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.emailConfirmationTokenService = emailConfirmationTokenService;
        this.emailService = emailService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Override
    public Optional<BaseUser> findByUserName(String username) {
        return userRepository.findBaseUserByEmail(username);
    }

    @Override
    @Transactional
    public int verifyEmailByTokenAndEnableUser(String tokenSerial) {
        EmailConfirmationToken emailConfirmationToken = emailConfirmationTokenService.confirmToken(tokenSerial);
        if (emailConfirmationToken.getBaseUser().isEnabled())
            throw new IllegalCallerException("Email is already verified!");
        if (emailConfirmationToken.getBaseUser().getRole().equals(UserRole.ROLE_TRADESMAN))
            userRepository.setTradesManStatus(emailConfirmationToken.getBaseUser().getId(), TradesManStatus.AWAITING_APPROVAL);
        return userRepository.toggleIsEmailVerified(emailConfirmationToken.getBaseUser().getEmail(), true);
    }

    private EmailConfirmationToken createEmailConfirmationTokenForUser(BaseUser user) {
        return EmailConfirmationToken.builder()
                .tokenSerial(UUID.randomUUID().toString())
                .baseUser(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(Policy.getEmailConfirmationTokenValidationTimeInMinutes()))
                .build();
    }

    @Transactional
    @Override
    public void sendEmailVerificationEmail(String userEmail) throws MessagingException {
        Optional<BaseUser> userOptional = findByUserName(userEmail);
        if (userOptional.isEmpty())
            return;//no exception is thrown if the email is invalid due to security reasons.
        if (userOptional.get().isEmailVerified())
            return;//no exception is thrown if the email is already verified due to security reasons.
        EmailConfirmationToken emailConfirmationTokenForUser = createEmailConfirmationTokenForUser(userOptional.get());
        emailConfirmationTokenService.save(emailConfirmationTokenForUser);
        String confirmationLink = Policy.getServerAddress() + "public-context/verify-email?tokenSerial=" + emailConfirmationTokenForUser.getTokenSerial();
        Email verificationEmail = Email.builder()
                .to(userEmail)
                .from(Policy.getVerificationEmailsAreSentFrom())
                .subject("Email Verification")
                .body(htmlBodyForEmailConfirmationBuilder(userOptional.get().getFirstName(), confirmationLink))
                .build();
        emailService.sendWithHTMLTemplate(verificationEmail);
        //todo use logger to log message if the email was sent (the end of method was reached)
    }

    @Override
    public Optional<BaseUser> findByEmailAndRole(String email, UserRole role) {
        return userRepository.findBaseUserByEmailAndRole(email, role);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsBaseUserByEmail(email);
    }

    @Override
    public boolean existsByEmailAndRole(String email, UserRole role) {
        return userRepository.existsBaseUserByEmailAndRole(email, role);
    }

    @Override
    public Optional<BaseUser> findByIdAndRole(Long id, UserRole role) {
        return userRepository.findBaseUserByIdAndRole(id, role);
    }

    @Override
    @Transactional
    public void editProfile(Long id, UserEditProfileDTO userEditProfileDTO) throws InterruptedException {
        SemaphoreUtil.acquireNewUserSemaphore();
        try {
            BaseUser currentUser = userRepository.findById(id).orElseThrow(() -> new InvalidInputException("user not found!"));
            String firstName = userEditProfileDTO.getFirstName();
            String lastName = userEditProfileDTO.getLastName();
            String email = userEditProfileDTO.getEmail();

            if (!Validation.isEmailValid(email))
                throw new InvalidInputException("Email pattern is not valid!");
            if (existsByEmail(email) && !userRepository.findBaseUserByEmail(email).get().getId().equals(currentUser.getId()))
                throw new InvalidInputException("This email already exists on database!");

            currentUser.setFirstName(firstName);
            currentUser.setLastName(lastName);
            currentUser.setEmail(email);
            userRepository.save(currentUser);
        } finally {
            SemaphoreUtil.releaseNewUserSemaphore();
        }

    }

    @Override
    @Transactional
    public void changePassword(String email, UserChangePasswordDTO userChPassDTO) throws NoSuchAlgorithmException, InvalidKeySpecException {
/**
 * bcrypt password encoder like many other modern encoders uses a randomly generated 'salt' to encode a string.
 * this means that each time you encode a certain string, a unique hash will be generated from the very same string.
 * to verify if a notHashed string matches a hashed one you cannot simply hash the string again and compare
 * the hashed values because hashed values for the same string would not match as different 'salts' where used to hash them.
 * in such cases you should use matches() method of the encoder as below.
 */
        BaseUser loggedInUser = userRepository.findBaseUserByEmail(email).orElseThrow(() -> new InvalidInputException("user not found"));
        boolean oldPassValid = bCryptPasswordEncoder.matches(userChPassDTO.getNotHashedOldPassword(), loggedInUser.getPassword());
        if (!oldPassValid)
            throw new InvalidInputException("Invalid credentials!");
        //validate new password
        boolean passwordValid = Validation.isPasswordValid(userChPassDTO.getNotHashedNewPassword());
        if (passwordValid) {
            String hashedPassword = bCryptPasswordEncoder.encode(userChPassDTO.getNotHashedNewPassword());
            loggedInUser.setPassword(hashedPassword);
            userRepository.save(loggedInUser);
        } else
            throw new InvalidInputException("The pattern of new password is not valid!");
    }


    private String htmlBodyForEmailConfirmationBuilder(String name, String confirmationLink) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                                
                  <meta charset="utf-8">
                  <meta http-equiv="x-ua-compatible" content="ie=edge">
                  <title>Email Confirmation</title>
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <style type="text/css">
                  /**
                   * Google webfonts. Recommended to include the .woff version for cross-client compatibility.
                   */
                  @media screen {
                    @font-face {
                      font-family: 'Source Sans Pro';
                      font-style: normal;
                      font-weight: 400;
                      src: local('Source Sans Pro Regular'), local('SourceSansPro-Regular'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/ODelI1aHBYDBqgeIAH2zlBM0YzuT7MdOe03otPbuUS0.woff) format('woff');
                    }
                    @font-face {
                      font-family: 'Source Sans Pro';
                      font-style: normal;
                      font-weight: 700;
                      src: local('Source Sans Pro Bold'), local('SourceSansPro-Bold'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/toadOcfmlt9b38dHJxOBGFkQc6VGVFSmCnC_l7QZG60.woff) format('woff');
                    }
                  }
                  /**
                   * Avoid browser level font resizing.
                   * 1. Windows Mobile
                   * 2. iOS / OSX
                   */
                  body,
                  table,
                  td,
                  a {
                    -ms-text-size-adjust: 100%; /* 1 */
                    -webkit-text-size-adjust: 100%; /* 2 */
                  }
                  /**
                   * Remove extra space added to tables and cells in Outlook.
                   */
                  table,
                  td {
                    mso-table-rspace: 0pt;
                    mso-table-lspace: 0pt;
                  }
                  /**
                   * Better fluid images in Internet Explorer.
                   */
                  img {
                    -ms-interpolation-mode: bicubic;
                  }
                  /**
                   * Remove blue links for iOS devices.
                   */
                  a[x-apple-data-detectors] {
                    font-family: inherit !important;
                    font-size: inherit !important;
                    font-weight: inherit !important;
                    line-height: inherit !important;
                    color: inherit !important;
                    text-decoration: none !important;
                  }
                  /**
                   * Fix centering issues in Android 4.4.
                   */
                  div[style*="margin: 16px 0;"] {
                    margin: 0 !important;
                  }
                  body {
                    width: 100% !important;
                    height: 100% !important;
                    padding: 0 !important;
                    margin: 0 !important;
                  }
                  /**
                   * Collapse table borders to avoid space between cells.
                   */
                  table {
                    border-collapse: collapse !important;
                  }
                  a {
                    color: #1a82e2;
                  }
                  img {
                    height: auto;
                    line-height: 100%;
                    text-decoration: none;
                    border: 0;
                    outline: none;
                  }
                  </style>
                                
                </head>
                <body style="background-color: #e9ecef;">
                                
                                
                  <!-- start body -->
                  <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                
                                 
                                
                    <!-- start hero -->
                    <tr>
                      <td align="center" bgcolor="#e9ecef">
                        <!--[if (gte mso 9)|(IE)]>
                        <table align="center" border="0" cellpadding="0" cellspacing="0" width="600">
                        <tr>
                        <td align="center" valign="top" width="600">
                        <![endif]-->
                        <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;">
                          <tr>
                            <td align="left" bgcolor="#ffffff" style="padding: 36px 24px 0; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; border-top: 3px solid #d4dadf;">
                              <h1 style="margin: 0; font-size: 32px; font-weight: 700; letter-spacing: -1px; line-height: 48px;"
                              """
                + ">Confirm Your Email Address,"
                + name + "! </h1>" +
                """
                                                 
                                                </td>
                                              </tr>
                                            </table>
                                            <!--[if (gte mso 9)|(IE)]>
                                            </td>
                                            </tr>
                                            </table>
                                            <![endif]-->
                                          </td>
                                        </tr>
                                        <!-- end hero -->
                                                    
                                        <!-- start copy block -->
                                        <tr>
                                          <td align="center" bgcolor="#e9ecef">
                                            <!--[if (gte mso 9)|(IE)]>
                                            <table align="center" border="0" cellpadding="0" cellspacing="0" width="600">
                                            <tr>
                                            <td align="center" valign="top" width="600">
                                            <![endif]-->
                                            <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;">
                                                    
                                              <!-- start copy -->
                                              <tr>
                                                <td align="left" bgcolor="#ffffff" style="padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;">
                                                 
                                                 <p style="margin: 0;">Tap the button below to confirm your email address. If you didn't create an account with <a href= 
                        """ + Policy.getServerAddress() +
                """
                        >MaktabProject</a>, you can safely delete this email.</p>
                        </td>
                        </tr>
                        <!-- end copy -->
                              
                        <!-- start button -->
                        <tr>
                          <td align="left" bgcolor="#ffffff">
                            <table border="0" cellpadding="0" cellspacing="0" width="100%">
                              <tr>
                                <td align="center" bgcolor="#ffffff" style="padding: 12px;">
                                  <table border="0" cellpadding="0" cellspacing="0">
                                    <tr>
                                      <td align="center" bgcolor="#1a82e2" style="border-radius: 6px;">
                                      
                                        <a href=
                                         """
                + confirmationLink +
                """ 
                                      target="_blank" style="display: inline-block; padding: 16px 36px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; color: #ffffff; text-decoration: none; border-radius: 6px;">Confirm</a>
                                      </td>
                                    </tr>
                                  </table>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                        <!-- end button -->
                              
                        <!-- start copy -->
                        <tr>
                          <td align="left" bgcolor="#ffffff" style="padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;">
                            <p style="margin: 0;">If that doesn't work, copy and paste the following link in your browser:</p>
                            <p style="margin: 0;">
                            """
                + "<a href= "
                + confirmationLink +
                """
                        target="_blank"> 
                        """ +
                confirmationLink +
                """
                                                     </a></p>
                                                    </td >
                                                  </tr >
                                                  <!--end copy-- >
                                                        
                                                  <!--start copy-- >
                                                  <tr >
                                                    <td align = "left" bgcolor = "#ffffff"
                        style = "padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px; border-bottom: 3px solid #d4dadf" >
                                                      <p style = "margin: 0;" > Cheers,<br > Sina Afzalsoltani </p >
                                                    </td >
                                                  </tr >
                                                  <!--end copy-- >
                                                        
                                                </table >
                                                <!--[if (gte mso 9)|(IE)]>
                                                </td >
                                                </tr >
                                                </table >
                                                <![endif]-- >
                                              </td >
                                            </tr >
                                            <!--end copy block -- >
                                                        
                                            <!--start footer-- >
                                            <tr >
                                              <td align = "center" bgcolor = "#e9ecef" style = "padding: 24px;" >
                                                <!--[if (gte mso 9)|(IE)]>
                                                <table align = "center" border = "0" cellpadding = "0" cellspacing = "0" width = "600" >
                                                <tr >
                                                <td align = "center" valign = "top" width = "600" >
                                                <![endif]-- >
                                                <table border = "0" cellpadding = "0" cellspacing = "0" width = "100%"
                        style = "max-width: 600px;" >
                                                        
                                                  <!--start permission-- >
                                                  <tr >
                                                    <td align = "center" bgcolor = "#e9ecef"
                        style = "padding: 12px 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 14px; line-height: 20px; color: #666;" >
                                                      <p style = "margin: 0;" > You received this email because we received a request
                        for email confirmation for your account.If you didn
                        't request email confirmation you can safely delete this email.</p>
                                </td >
                                                  </tr >
                                                  <!--end permission-- >
                                                        
                                                        
                                                </table >
                                                <!--[if (gte mso 9)|(IE)]>
                                                </td >
                                                </tr >
                                                </table >
                                                <![endif]-- >
                                              </td >
                                            </tr >
                                            <!--end footer-- >
                                                        
                                          </table >
                                          <!--end body-- >
                                                        
                                        </body >
                                        </html >
                                """;
    }
}
