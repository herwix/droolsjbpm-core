package com.iterranux.droolsjbpmCore.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;


/**
 * Workaround to make standalone TransactionManagers accessible via JNDI.
 */
public class TransactionManagerJNDIRegistrator {

    private static final Log log = LogFactory.getLog(TransactionManagerJNDIRegistrator.class);
    
    private String userTransactionLookup;
    
    private String transactionManagerLookup;
    
    private TransactionManager transactionManager;

    private UserTransaction userTransaction;
    
    @PostConstruct
    public void init(){
        log.debug("Registering TX to JNDI.");
        try {
            SimpleNamingContextBuilder.emptyActivatedContextBuilder();
            Context context = new InitialContext();

            context.bind(userTransactionLookup, userTransaction);
            context.bind(transactionManagerLookup, transactionManager);
                    
        } catch (NamingException e){
            log.error("JNDI Registration of the Transasction Manager failed.", e);
        }

    }

    public void setUserTransaction(UserTransaction userTransaction) {
        this.userTransaction = userTransaction;
    }

    public void setUserTransactionLookup(String userTransactionLookup) {
        this.userTransactionLookup = userTransactionLookup;
    }

    public void setTransactionManagerLookup(String transactionManagerLookup) {
        this.transactionManagerLookup = transactionManagerLookup;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

}
