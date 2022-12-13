package shuhuai.wheremoney.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class BeanGetter implements ApplicationContextAware {

    public static ApplicationContext context;

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext context) throws BeansException {
        BeanGetter.context = context;
    }

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    public static ApplicationContext getContext() {
        return context;
    }
}