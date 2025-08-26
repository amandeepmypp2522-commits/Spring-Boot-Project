package com.amandea.app.ws;

//In this situation AuthenticationFilter class is not a spring bean but still need to access spring beans [UserDto]
// it is helper class which makes the above statement possible.

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringApplicationContext implements ApplicationContextAware {

    private static ApplicationContext CONTEXT;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CONTEXT = applicationContext;
    }

    public static Object getBean(String beanName){
        return CONTEXT.getBean(beanName);
    }
}
