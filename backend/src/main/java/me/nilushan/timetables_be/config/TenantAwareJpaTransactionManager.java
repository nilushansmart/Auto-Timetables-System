package me.nilushan.timetables_be.config;

import jakarta.persistence.EntityManagerFactory;
import me.nilushan.timetables_be.security.TenantContext;
import org.hibernate.Session;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.PreparedStatement;

public class TenantAwareJpaTransactionManager extends JpaTransactionManager {

    public TenantAwareJpaTransactionManager(EntityManagerFactory emf) {
        super(emf);
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        super.doBegin(transaction, definition);
        Long schoolId = TenantContext.getSchoolId();
        if (schoolId != null) {
            EntityManagerHolder emHolder = (EntityManagerHolder)
                    TransactionSynchronizationManager.getResource(obtainEntityManagerFactory());
            if (emHolder != null) {
                Session session = emHolder.getEntityManager().unwrap(Session.class);
                session.doWork(connection -> {
                    try (PreparedStatement ps = connection.prepareStatement("SELECT set_config('app.school_id', ?, true)")) {
                        ps.setString(1, String.valueOf(schoolId));
                        ps.execute();
                    }
                });
            }
        }
    }
}
