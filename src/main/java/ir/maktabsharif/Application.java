package ir.maktabsharif;

import ir.maktabsharif.model.enumeration.TradesManStatus;
import ir.maktabsharif.repository.AdvancedUserSearchDAO;
import ir.maktabsharif.service.AdminService;
import ir.maktabsharif.service.CategoryService;
import ir.maktabsharif.service.CustomerService;
import ir.maktabsharif.service.dto.request.AdvancedUserSearchDTO;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;


@SpringBootApplication
public class Application {
    @Autowired
    CategoryService categoryService;
    @Autowired
    AdminService adminService;
    @Autowired
    CustomerService customerService;

    public Application(AdvancedUserSearchDAO advancedUserSearchDAO) {
        this.advancedUserSearchDAO = advancedUserSearchDAO;
    }


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private final AdvancedUserSearchDAO advancedUserSearchDAO;
@Bean
public EntityManager getEntityManagerBean(EntityManager entityManager){
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
    @PostConstruct
    public void test(){
        AdvancedUserSearchDTO dto = AdvancedUserSearchDTO.builder().tradesManSubCategoryId(53L).build();
        System.out.println(advancedUserSearchDAO.findUsers(dto));
    }
}




