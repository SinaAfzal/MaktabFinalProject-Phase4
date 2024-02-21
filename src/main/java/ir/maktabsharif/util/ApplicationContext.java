package ir.maktabsharif.util;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;


public class ApplicationContext {
//    private static final EntityManager ENTITY_MANAGER =
//            Persistence.createEntityManagerFactory("default").
//                    createEntityManager();
    private static final ValidatorFactory VALIDATOR_FACTORY = Validation.byDefaultProvider().configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory();
    private static final Validator VALIDATOR = VALIDATOR_FACTORY.usingContext()
            .messageInterpolator(new ParameterMessageInterpolator())
            .getValidator();

//    private static AdminRepository adminRepository;
//    private static CategoryRepository categoryRepository;
//    private static CustomerRepository customerRepository;
//    private static ProposalRepository proposalRepository;
//    private static TaskRepository taskRepository;
//    private static TradesManRepository tradesManRepository;
//
//
//
//    private static AdminService adminService;
//    private static CategoryService categoryService;
//    private static CustomerService customerService;
//    private static ProposalService proposalService;
//    private static TaskService taskService;
//    private static TradesManService tradesManService;
//
//
//    public static AdminService getAdminService() {
//        if (adminService == null)
//            adminService = new AdminServiceImpl(getAdminRepository());
//        return adminService;
//    }
//
//    public static CategoryService getCategoryService() {
//        if (categoryService == null)
//            categoryService = new CategoryServiceImpl(getCategoryRepository(), getTradesManRepository());
//        return categoryService;
//    }
//
//    public static CustomerService getCustomerService() {
//        if (customerService == null)
//            customerService = new CustomerServiceImpl(getCustomerRepository(), getTaskService());
//        return customerService;
//    }
//
//    public static ProposalService getProposalService() {
//        if (proposalService == null)
//            proposalService = new ProposalServiceImpl(getProposalRepository(), getTaskRepository(), getTradesManService(), getCategoryRepository());
//        return proposalService;
//    }
//
//    public static TaskService getTaskService() {
//        if (taskService == null)
//            taskService = new TaskServiceImpl(getTaskRepository(), getCategoryRepository(), getProposalRepository(), getTradesManRepository(), getCustomerRepository());
//        return taskService;
//    }
//
//    public static TradesManService getTradesManService() {
//        if (tradesManService == null)
//            tradesManService = new TradesManServiceImpl(getTradesManRepository(),getTaskRepository());
//        return tradesManService;
//    }
//
//
//    private static AdminRepository getAdminRepository() {
//        if (adminRepository == null)
//            adminRepository = new AdminRepositoryImpl(ENTITY_MANAGER);
//        return adminRepository;
//    }
//
//    private static CategoryRepository getCategoryRepository() {
//        if (categoryRepository == null)
//            categoryRepository = new CategoryRepositoryImpl(ENTITY_MANAGER);
//        return categoryRepository;
//    }
//
//    private static CustomerRepository getCustomerRepository() {
//        if (customerRepository == null)
//            customerRepository = new CustomerRepositoryImpl(ENTITY_MANAGER);
//        return customerRepository;
//    }
//
//    private static ProposalRepository getProposalRepository() {
//        if (proposalRepository == null)
//            proposalRepository = new ProposalRepositoryImpl(ENTITY_MANAGER);
//        return proposalRepository;
//    }
//
//    private static TaskRepository getTaskRepository() {
//        if (taskRepository == null)
//            taskRepository = new TaskRepositoryImpl(ENTITY_MANAGER);
//        return taskRepository;
//    }
//
//    private static TradesManRepository getTradesManRepository() {
//        if (tradesManRepository == null)
//            tradesManRepository = new TradesManRepositoryImpl(ENTITY_MANAGER);
//        return tradesManRepository;
//    }
//
//
//    public static EntityManager getEntityManager() {
//        return ENTITY_MANAGER;
//    }

    public static Validator getValidator() {
        return VALIDATOR;
    }


}
