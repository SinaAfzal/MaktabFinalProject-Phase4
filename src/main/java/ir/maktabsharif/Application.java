package ir.maktabsharif;

import ir.maktabsharif.service.CustomerService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnit;
import org.checkerframework.framework.qual.PostconditionAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class Application {
    @Autowired
    private final CustomerService customerService;

    public Application(CustomerService customerService) {
        this.customerService = customerService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    @EventListener(ApplicationReadyEvent.class)
//    public void test() throws MessagingException {
//        emailService.sendWithHTMLTemplate(new Email("sds", Policy.getVerificationEmailsAreSentFrom(),"fasfasd@asdasd.com","ededed"));
//    }


    @Bean
    public EntityManager getEntityManagerBean(EntityManager entityManager) {
        return entityManager;
    }


//    @PostConstruct
//    public void test() throws NoSuchAlgorithmException, InvalidKeySpecException, InterruptedException {
////register admin
//        AdminRegistrationDTO adminRegistrationDTO =
//                new AdminRegistrationDTO(
//                        "admin",
//                        "admin",
//                        "admin@admin.ir",
//                        "Admin123456");
//
//        adminService.register(adminRegistrationDTO);
////login admin
//        LoginDTO loginDTO = new LoginDTO();
//        loginDTO.setEmail("admin@admin.ir");
//        loginDTO.setNotHashedPassword("Admin123456");
//
//        adminService.login(loginDTO);
//
//        //add category
//        categoryService.addCategory(new CategoryDTO("Freight and moving", null, null, null));
//
//        //add subcategory
//        Long parentId = categoryService.findByCategoryName("Freight and moving").getId();
//        categoryService.addCategory(
//                new CategoryDTO(
//                        "Car electricity",
//                        parentId,
//                        "fixing car electricity issues, changing car battery, etc",
//                        700D)
//        );
//
//        //edit category
//        CategoryDTO categoryDTO = new CategoryDTO("Car electricity", 1L, "this description is edited", 600D);
//        Long categoryId = categoryService.findByCategoryName("Car electricity").getId();
//        categoryService.editCategory(categoryId, categoryDTO);
//
//
//    }

//    @PostConstruct
//    public void loginCustomerWithDoneTask() throws NoSuchAlgorithmException, InvalidKeySpecException {
//        customerService.login(new LoginDTO("customerForTaskTest@gmail.com","Sina123456"));
//
//    }
//    @PostConstruct
//    public void test(){
//        AdvancedUserSearchDTO dto = AdvancedUserSearchDTO.builder().tradesManSubCategoryId(53L).build();
//        System.out.println(advancedUserSearchDAO.findUsers(dto));
//    }
//    @PostConstruct
//    public void test() throws NoSuchAlgorithmException, InvalidKeySpecException {
//        System.out.println(adminService.findByEmail("testeidhashsneidneid@edined.com"));
//   //adminService.login(new LoginDTO("HASHBCRYPTsneidneid@edined.com","Tews_ab1fbd733e76"));
//   ;
//
//    }


//    @PostConstruct
//    public void test(){
//        System.out.println(bCryptPasswordEncoder.encode("Super@admin123"));
//    }

//    @PostConstruct
//    public void test(){
//        customerService.deleteCustomerById(2L);
//    }

}




