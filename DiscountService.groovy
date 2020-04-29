package com.ololoCompany.meetizer.business.service

import com.ololoCompany.meetizer.business.data.domain.Discount
import com.ololoCompany.meetizer.business.data.domain.DiscountType
import com.ololoCompany.meetizer.business.data.domain.Event
import com.ololoCompany.meetizer.business.data.service.DiscountDataService
import com.ololoCompany.meetizer.business.data.service.EventDataService
import com.ololoCompany.meetizer.business.exception.NotValidModelException
import com.ololoCompany.meetizer.business.model.promo.DiscountRequestDto
import com.ololoCompany.meetizer.business.model.promo.DiscountResponseDto
import com.ololoCompany.meetizer.business.model.TicketTypeModel
import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
@SuppressWarnings("GrMethodMayBeStatic")
class DiscountService {

    @Autowired private DiscountDataService discountDataService
    @Autowired private EventDataService eventDataService

    @Autowired private TicketTypeService ticketTypeService

    /**
     * Создание скидки
     * @param discountRequestDto
     */
    void create(List<DiscountRequestDto> discountRequestDtos, String userId){

        discountRequestDtos.each {
            this.validateDiscountRequestDto(it)
        }

        discountDataService.create(discountRequestDtos, userId)
    }

    private validateDiscountRequestDto(DiscountRequestDto discountRequestDto){

        DateTime nowDateTime = new DateTime()
        Long now = new DateTime(nowDateTime.year, nowDateTime.monthOfYear, nowDateTime.dayOfMonth, 0 , 0,0).toDate().getTime()

        if((discountRequestDto.amount && (discountRequestDto.amount <= 0 || discountRequestDto.amount > 100)) ||
                discountRequestDto.start < now || discountRequestDto.end < now ||
                discountRequestDto.end < discountRequestDto.start || discountRequestDto.ticketTypeIds.empty){

            log.error("Incorrect discount data, percent: [$discountRequestDto.amount]")
            throw new NotValidModelException("Incorrect discount data, amount: [$discountRequestDto.amount]", 'exception.model.not.valid')
        }
    }

    /**
     * Включение/выключение скидки
     * @param discountId
     */
    void onOffDiscount(String discountId){

        discountDataService.onOffDiscount(discountId)
    }

    /**
     * Удаление скидки
     * @param discountId
     */
    void delete(String discountId){

        discountDataService.delete(discountId)
    }

    /**
     * Все скидки по пользователю
     * @param userId
     * @return
     */
    List<DiscountResponseDto> findAllDiscountsByUserId(String userId){

        List<Discount> discounts = discountDataService.findAllByUserId(userId)

        return discounts.collect { DiscountResponseDto.buildFromDomain(it, [])}
    }

    /**
     * Все скидки по событию
     * @param eventId
     * @return
     */
    List<DiscountResponseDto> findAllDiscountsByEventId(String eventId, String userId){

        List<Discount> discounts = discountDataService.findAllByEventId(eventId)

        List<TicketTypeModel> ticketTypes = ticketTypeService.getAllTicketTypeByEventIdWithValidate(eventId, userId)

        return discounts.collect { DiscountResponseDto.buildFromDomain(it, ticketTypes)}
    }

    /**
     * Все акутальные скидки по событию
     * @param eventId
     * @return
     */
    List<DiscountResponseDto> findAllCurrentDiscountsByEventId(String eventId){

        Date now = new Date()

        List<Discount> discounts = discountDataService.findAllCurrentByEventId(eventId)

        discounts = discounts.findAll {it.end > now.time}

        Event event = eventDataService.findByEventId(eventId)

        List<TicketTypeModel> ticketTypes = ticketTypeService.getAllTicketTypeByEventIdAndNotExpired(event.id)

        return discounts.collect { DiscountResponseDto.buildFromDomain(it, ticketTypes)}
    }

    /**
     * Все скидки по идентификаторам
     * @param ids
     * @return
     */
    List<DiscountResponseDto> findAllByIds(List<String> ids){

        List<Discount> discounts = discountDataService.findAllByIds(ids)

        return discounts.collect { DiscountResponseDto.buildFromDomain(it, [])}
    }
}
