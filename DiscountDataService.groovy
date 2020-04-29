package com.ololoCompany.meetizer.business.data.service

import com.ololoCompany.meetizer.business.data.domain.Discount
import com.ololoCompany.meetizer.business.data.domain.Event
import com.ololoCompany.meetizer.business.data.repository.DiscountRepository
import com.ololoCompany.meetizer.business.exception.DiscountNotFoundException
import com.ololoCompany.meetizer.business.model.promo.DiscountRequestDto
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class DiscountDataService {

    @Autowired private DiscountRepository discountRepository

    @Autowired private EventDataService eventDataService

    /**
     * Создание скидки
     * @param dto
     */
    void create(List<DiscountRequestDto> dtos, String userId){

        log.debug("Create list of discounts. User ID [$userId]")

        List<Discount> discounts = dtos.collect {
            return new Discount(
                amount: it.amount,
                start: it.start,
                end: it.end,
                count: it.count,
                ticketTypeIds: it.ticketTypeIds,
                type: it.type,
                isAdd: it.isAdd,
                isOn: true,
                isShow: it.isShow,
                countDone: 0,
                eventId: it.eventId,
                userId: userId
        )}

        discountRepository.saveAll(discounts)

        log.debug("Successfully created discounts size [${discounts.size()}]")
    }

    /**
     * Удвление скидки
     * @param discountId
     */
    void delete(String discountId){

        log.debug("Delete discount ID [$discountId]")

        discountRepository.deleteById(discountId)
    }

    /**
     * Включение/выключение скидки
     * @param discountId
     */
    void onOffDiscount(String id){

        log.debug("Turning on/off discount ID [$id]")

        Discount discount = this.findById(id)

        discount.isOn = !discount.isOn

        discountRepository.save(discount)
    }

    /**
     * Изменение количества сработавших скидок
     * @param id
     * @param count
     */
    void changeCountDone(String id){

        log.debug("Change count done for discount ID [${id}]")

        Discount discount = this.findById(id)

        discount.countDone++

        discountRepository.save(discount)
    }

    /**
     * Поиск скидки по идентификатору
     * @param id
     * @return
     */
    Discount findById(String id){

        log.debug("Find discount by ID [$id]")

        Discount discount = discountRepository.findById(id).orElse(null)

        if(!discount){
            log.debug("Not found discount by ID [$id]")
            throw new DiscountNotFoundException("Not found discount by ID [$id]", "discount.not.found.exception")
        }

        log.debug("Found discount ID [$discount.id]")

        return discount
    }

    /**
     * Поиск скидок по пользователю
     * @param userId
     * @return
     */
    List<Discount> findAllByUserId(String userId){

        log.debug("Find all discounts by user ID [$userId]")

        List<Discount> discounts = discountRepository.findAllByUserId(userId)

        if(!discounts){
            log.debug("Not found any discounts")
            return []
        }

        log.debug("Found discounts size [${discounts.size()}]")

        return discounts
    }

    /**
     * Поиск скидок по событию
     * @param userId
     * @return
     */
    List<Discount> findAllByEventId(String eventId){

        log.debug("Find all discounts by event ID [$eventId]")

        List<Discount> discounts = discountRepository.findAllByEventId(eventId)

        if(!discounts){
            log.debug("Not found any discounts")
            return []
        }

        log.debug("Found discounts size [${discounts.size()}]")

        return discounts
    }

    /**
     * Поиск акутальных скидок по событию
     * @param eventId
     * @return
     */
    List<Discount> findAllCurrentByEventId(String eventId){

        log.debug("Find all discounts by event ID [$eventId]")

        Event event = eventDataService.findByEventId(eventId)

        Long now = System.currentTimeMillis()

        List<Discount> discounts = discountRepository.findAllByEventIdAndStartLessThanAndEndGreaterThanAndIsOnTrue(event.id, now, now)

        if(!discounts){
            log.debug("Not found any discounts")
            return []
        }

        log.debug("Found discounts size [${discounts.size()}]")

        return discounts
    }

    /**
     * Поиск скидок по типу билета
     * @param userId
     * @return
     */
    List<Discount> findAllByTicketTypeId(String ticketTypeId){

        log.debug("Find all discounts by ticketType ID [$ticketTypeId]")

        List<Discount> discounts = discountRepository.findAllByTicketTypeIdsIn(ticketTypeId)

        if(!discounts){
            log.debug("Not found any discounts")
            return []
        }

        log.debug("Found discounts size [${discounts.size()}]")

        return discounts
    }

    /**
     * Поиск скидок по идентификаторам
     * @param ids
     * @return
     */
    List<Discount> findAllByIds(List<String> ids){

        log.debug("Find all discounts by IDs size [${ids.size()}]")

        List<Discount> discounts = discountRepository.findAllByIdIn(ids)

        if(!discounts){
            log.debug("Not found any discounts")
            return []
        }

        log.debug("Found discounts size [${discounts.size()}]")

        return discounts
    }

    void saveAll(List<Discount> discounts){

        log.debug("Save discounts size [$discounts.size()]")

        discountRepository.saveAll(discounts)
    }
}
