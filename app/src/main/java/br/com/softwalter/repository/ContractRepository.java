package br.com.softwalter.repository;

import br.com.softwalter.entity.Contract;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class ContractRepository {

    private Session session;

    public ContractRepository(Session session) {
        this.session = session;
    }

    public List<Contract> findContractsByPersonId(Long personId) {
        Query<Contract> query = session.createQuery("FROM Contract WHERE person.id = :personId", Contract.class);
        query.setParameter("personId", personId);
        return query.getResultList();
    }
}
