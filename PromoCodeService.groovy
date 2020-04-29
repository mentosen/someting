package com.ololoCompany.meetizer.business.service

import com.ololoCompany.meetizer.business.data.domain.DiscountPayType
import com.ololoCompany.meetizer.business.data.domain.PromoCode
import com.ololoCompany.meetizer.business.data.service.PromoCodeDataService
import com.ololoCompany.meetizer.business.exception.NotEnoughTicketException
import com.ololoCompany.meetizer.business.exception.NotValidModelException
import com.ololoCompany.meetizer.business.exception.WrongPromoCodeException
import com.ololoCompany.meetizer.business.model.promo.PromoCodeRequestBatchDto
import com.ololoCompany.meetizer.business.model.promo.PromoCodeRequestDto
import com.ololoCompany.meetizer.business.model.promo.PromoCodeResponseDto
import com.ololoCompany.meetizer.business.model.TicketTypeModel
import com.ololoCompany.meetizer.business.model.promo.PromoCodeViewModel
import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Slf4j
@Service
@SuppressWarnings("GrMethodMayBeStatic")
class PromoCodeService {

    @Autowired private PromoCodeDataService promoCodeDataService

    @Autowired private TicketTypeService ticketTypeService

    /**
     * Создание промокода
     * @param promoCodeRequestDto
     */
    void create(PromoCodeRequestDto promoCodeRequestDto){

        this.validatePromoCodeRequestDto(promoCodeRequestDto)

        promoCodeDataService.create(promoCodeRequestDto)
    }

    private validatePromoCodeRequestDto(PromoCodeRequestDto promoCodeRequestDto){

        DateTime nowDateTime = new DateTime()
        Long now = new DateTime(nowDateTime.year, nowDateTime.monthOfYear, nowDateTime.dayOfMonth, 0 , 0,0).toDate().getTime()

        PromoCode promoCode = promoCodeDataService.findByCodeAndEventIdOrNull(promoCodeRequestDto.code, promoCodeRequestDto.eventId)

        if(promoCode || (promoCodeRequestDto.amount && promoCodeRequestDto.amount <= 0) ||
                (promoCodeRequestDto.amount && promoCodeRequestDto.amount > 100 && promoCodeRequestDto.payType == DiscountPayType.PERCENT) ||
                promoCodeRequestDto.start < now || promoCodeRequestDto.end < now ||
                promoCodeRequestDto.end < promoCodeRequestDto.start || promoCodeRequestDto.code.size() > 20){

            log.error("Incorrect promo code data, percent: [$promoCodeRequestDto.amount] user ID [$promoCodeRequestDto.userId]")
            throw new NotValidModelException("Incorrect promo code percent, percent: [$promoCodeRequestDto.amount] user ID [$promoCodeRequestDto.userId]", 'exception.model.not.valid')
        }
    }

    private validatePromoCodeRequestBatchDto(PromoCodeRequestBatchDto batchDto){

        DateTime nowDateTime = new DateTime()
        Long now = new DateTime(nowDateTime.year, nowDateTime.monthOfYear, nowDateTime.dayOfMonth, 0 , 0,0).toDate().getTime()

        if((batchDto.amount && batchDto.amount <= 0) ||
                batchDto.start < now || batchDto.end < now ||
                batchDto.end < batchDto.start){

            log.error("Incorrect promo code data, amount: [$batchDto.amount] user ID [$batchDto.userId]")
            throw new NotValidModelException("Incorrect promo code amount, amount: [$batchDto.amount] user ID [$batchDto.userId]", 'exception.model.not.valid')
        }
    }

    /**
     * Создание пакета промокодов
     * @param promoCodeRequestDto
     */
    void createBatch(PromoCodeRequestBatchDto batchDto){

        this.validatePromoCodeRequestBatchDto(batchDto)

        promoCodeDataService.createBatch(batchDto)
    }

    /**
     * Включение/выключение промокода
     * @param id
     */
    void onOffPromoCode(String id){

        promoCodeDataService.onOffPromoCode(id)
    }

    /**
     * Удаление промокода
     * @param promoCodeRequestDto
     */
    void delete(String id){

        promoCodeDataService.delete(id)
    }

    /**
     * Все промокоды по пользователю
     * @param userId
     * @return
     */
    List<PromoCodeResponseDto> findAllPromoCodesByUserId(String userId){

        List<PromoCode> promoCodes = promoCodeDataService.findAllByUserId(userId)

        return promoCodes.collect { PromoCodeResponseDto.buildFromDomain(it, [])}
    }

    /**
     * Все промокоды по событию
     * @param eventId
     * @return
     */
    PromoCodeViewModel findAllPromoCodesByEventId(String eventId, int page, int size){

        Page<PromoCode> promoCodes = promoCodeDataService.findAllByEventId(eventId, page, size)

        List<TicketTypeModel> ticketTypes = ticketTypeService.getAllActiveTicketTypeByEventId(eventId)

        List<PromoCodeResponseDto> promoCodeResponseDtos = []
        promoCodeResponseDtos = promoCodes.collect { PromoCodeResponseDto.buildFromDomain(it, ticketTypes)}

        PromoCodeViewModel promoCodeViewModel = new PromoCodeViewModel(
                content: promoCodeResponseDtos, number: promoCodes.number,
                size: promoCodes.size, totalElements: promoCodes.totalElements, totalPages: promoCodes.totalPages)

        return promoCodeViewModel
    }

    /**
     * Все промокоды по идентификаторам
     * @param ids
     * @return
     */
    List<PromoCodeResponseDto> findAllByIds(List<String> ids){

        List<PromoCode> promoCodes = promoCodeDataService.findAllByIds(ids)

        return promoCodes.collect { PromoCodeResponseDto.buildFromDomain(it, [])}
    }

    /**
     * Промокод по тексту
     * @param ids
     * @return
     */
    PromoCodeResponseDto findPromocodeByText(String eventId, String code){

        PromoCode promoCode = promoCodeDataService.findByCodeAndEventIdOrReturnNull(eventId, code)

        return promoCode ? PromoCodeResponseDto.buildFromDomain(promoCode, []) : null
    }

    /**
     * Промокод по тексту
     * @param ids
     * @return
     */
    PromoCodeResponseDto checkPromoCode(String eventId, String code){

        PromoCode promoCode = promoCodeDataService.findByCodeAndEventId(eventId, code)

        this.validatePromoCode(promoCode)

        List<TicketTypeModel> ticketTypes = ticketTypeService.getAllTicketTypeByEventIdAndNotExpired(eventId)

        return PromoCodeResponseDto.buildFromDomain(promoCode, ticketTypes)
    }

    private validatePromoCode(PromoCode promoCode){

        Long now = new Date().getTime()
        String localizedMessage = "", message = ""

        if(promoCode.start > now){
            log.error("Promocode start date after now, ID [$promoCode.id]")
            message += "Promocode start date after now, ID [$promoCode.id] "
            localizedMessage = 'promocode.start.after.now'
        }

        if(promoCode.end < now){
            log.error("Promocode end date before now, ID [$promoCode.id]")
            message += "Promocode end date before now, ID [$promoCode.id] "
            localizedMessage = 'promocode.end.before.now'
        }

        if(!promoCode.isOn){
            log.error("Promocode is off, ID [$promoCode.id]")
            message += "Promocode is off, ID [$promoCode.id] "
            localizedMessage = 'promocode.is.off'
        }

        if(promoCode.count <= 0 && !promoCode.isUnlimited){
            log.error("Promocode count lower or equal 0, ID [$promoCode.id]")
            message += "Promocode count lower or equal 0, ID [$promoCode.id] "
            localizedMessage = 'promocode.count.left'
        }

        if(!message.empty){
            throw new WrongPromoCodeException(message, localizedMessage)
        }
    }

    /**
     * Поиск активного промокода
     * @param ids
     * @return
     */
    List<PromoCodeResponseDto> findActivePromocode(String eventId){

        List<PromoCode> promoCodes = promoCodeDataService.findActiveCodeByEventId(eventId)

        return promoCodes.collect{PromoCodeResponseDto.buildFromDomain(it, [])}
    }

    /**
     * Поиск активного промокода
     * @param ids
     * @return
     */
    Map checkActivePromocode(String eventId){

        List<PromoCode> promoCodes = promoCodeDataService.findActiveCodeByEventId(eventId)

        return [ticketTypeIds: promoCodes.ticketTypeIds.flatten().unique()]
    }

    /**
     * Уменьшение количества оставшихся билетов по промо коду
     * @param id
     */
    void decrementPromoCode(String id){

        log.info("Decrement promocode id: [$id] left count")

        PromoCode promoCode = promoCodeDataService.findById(id)

        if(promoCode.count > 0){
            promoCode.isUnlimited ?: promoCode.count--
            promoCode.countDone++

            promoCodeDataService.save(promoCode)
        }
    }


}
