package pl.ds.websight.validation.impl;

import org.apache.bval.jsr.ApacheValidationProvider;
import org.apache.sling.commons.classloader.DynamicClassLoaderManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;
import java.util.Set;

@Component
public class ValidatorImpl implements Validator {

    private Validator validator;
    private ValidatorFactory validatorFactory;

    @Reference
    private DynamicClassLoaderManager classLoaderManager;

    @Activate
    void activate() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoaderManager.getDynamicClassLoader());
            validatorFactory = Validation.byProvider(ApacheValidationProvider.class).configure().buildValidatorFactory();
            validator = validatorFactory.getValidator();
        } finally {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
    }

    @Deactivate
    void deactivate() {
        if (validatorFactory != null) {
            validatorFactory.close();
        }
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        return validator.validate(object, groups);
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
        return validator.validateProperty(object, propertyName, groups);
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
        return validator.validateValue(beanType, propertyName, value, groups);
    }

    @Override
    public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
        return validator.getConstraintsForClass(clazz);
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        return validator.unwrap(type);
    }

    @Override
    public ExecutableValidator forExecutables() {
        return validator.forExecutables();
    }
}