package ru.otus;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.otus.core.model.Address;
import ru.otus.core.model.ExcludeJson;
import ru.otus.core.model.Phone;
import ru.otus.core.model.User;
import ru.otus.core.service.DBServiceUser;
import ru.otus.core.service.DbServiceUserImpl;
import ru.otus.hibernate.HibernateProxyTypeAdapter;
import ru.otus.hibernate.HibernateUtils;
import ru.otus.hibernate.dao.UserDaoHibernate;
import ru.otus.hibernate.sessionmanager.SessionManagerHibernate;

import java.util.List;

@Configuration
@ComponentScan
@EnableWebMvc
public class ApplConfig implements WebMvcConfigurer {

    private final ApplicationContext applicationContext;

    public ApplConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public DBServiceUser dbServiceUser(UserDaoHibernate userDaoHibernate) {
        return new DbServiceUserImpl(userDaoHibernate);
    }

    @Bean
    public UserDaoHibernate userDaoHibernate(SessionManagerHibernate sessionManagerHibernate) {
        return new UserDaoHibernate(sessionManagerHibernate);
    }

    @Bean
    public SessionManagerHibernate sessionManagerHibernate() {
        Class<?>[] classes = {User.class, Phone.class, Address.class};
        SessionFactory sessionFactory = HibernateUtils.buildSessionFactory("hibernate.cfg.xml", classes);
        return new SessionManagerHibernate(sessionFactory);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(createGsonHttpMessageConverter());
    }

    private GsonHttpMessageConverter createGsonHttpMessageConverter() {
        var excludeFieldsJson = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return fieldAttributes.getAnnotation(ExcludeJson.class) != null;
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        };

        Gson gson = new GsonBuilder().registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY)
                .serializeNulls()
                .addSerializationExclusionStrategy(excludeFieldsJson)
                .setPrettyPrinting()
                .create();

        GsonHttpMessageConverter gsonConverter = new GsonHttpMessageConverter();
        gsonConverter.setGson(gson);

        return gsonConverter;
    }
}
