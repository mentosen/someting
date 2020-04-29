package com.ololoCompany.meetizer.business.data.repository

import com.ololoCompany.meetizer.business.data.domain.Discount
import org.springframework.data.mongodb.repository.MongoRepository

interface DiscountRepository extends MongoRepository<Discount, String>{

    List<Discount> findAllByUserId(String userId)

    List<Discount> findAllByEventId(String eventId)

    List<Discount> findAllByEventIdAndStartLessThanAndEndGreaterThanAndIsOnTrue(String eventId, Long start, Long end)

    List<Discount> findAllByTicketTypeIdsIn(String ticketTypeId)

    List<Discount> findAllByIdIn(List<String> ids)

}