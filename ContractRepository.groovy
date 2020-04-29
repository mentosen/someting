package com.ololoCompany.meetizer.business.data.repository

import com.ololoCompany.meetizer.business.data.domain.Contract
import org.springframework.data.mongodb.repository.MongoRepository

interface ContractRepository extends MongoRepository<Contract, String>{

    List<Contract> findAllByUserId(String userId)

    Contract findByIdAndUserId(String id, String userId)
}