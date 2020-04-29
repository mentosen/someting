package com.ololoCompany.meetizer.business.data.service


import com.ololoCompany.meetizer.business.data.domain.PromoCode
import com.ololoCompany.meetizer.business.data.repository.PromoCodeRepository
import com.ololoCompany.meetizer.business.exception.PromoCodeNotFoundException
import com.ololoCompany.meetizer.business.model.promo.PromoCodeRequestBatchDto
import com.ololoCompany.meetizer.business.model.promo.PromoCodeRequestDto
import com.ololoCompany.meetizer.business.util.CodeUtils
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Slf4j
@Service
class PromoCodeDataService {

    @Autowired private PromoCodeRepository promoCodeRepository

    /**
     * Создание промокода
     * @param dto
     */
    void create(PromoCodeRequestDto dto){

        log.debug("Create promoCode. Percent: [$dto.amount] code: [$dto.code] user ID [$dto.userId]")

        PromoCode promoCode = new PromoCode(
                amount: dto.amount,
                start: dto.start,
                end: dto.end,
                code: dto.code,
                count: dto.count,
                countDone: 0,
                isAdd: dto.isAdd,
                isOn: true,
                isUnlimited: dto.isUnlimited,
                ticketTypeIds: dto.ticketTypeIds,
                payType: dto.payType,
                eventId: dto.eventId,
                userId: dto.userId
        )

        promoCode = promoCodeRepository.save(promoCode)

        log.debug("Successfully created promoCode ID [$promoCode.id]")
    }

    /**
     * Создание промокода
     * @param dto
     */
    void createBatch(PromoCodeRequestBatchDto dto){

        log.debug("Create promoCode batch. Percent: [$dto.amount] code count: [$dto.codeCount] user ID [$dto.userId]")

        List<PromoCode> promoCodes = []

        for (int i = 0; i < dto.codeCount; i++) {
            promoCodes.add(new PromoCode(
                    amount: dto.amount,
                    start: dto.start,
                    end: dto.end,
                    code: dto.prefix + CodeUtils.generateRandomCode(dto.symbolCount),
                    count: dto.count,
                    countDone: 0,
                    isAdd: dto.isAdd,
                    isOn: true,
                    ticketTypeIds: dto.ticketTypeIds,
                    payType: dto.payType,
                    eventId: dto.eventId,
                    userId: dto.userId
            ))
        }

        promoCodeRepository.saveAll(promoCodes)

        log.debug("Successfully created promoCodes size: [$promoCodes.size()]")
    }

    /**
     * Включение/выключение промокода
     * @param id
     */
    void onOffPromoCode(String id){

        log.debug("Turning on/off promoCode ID [$id]")

        PromoCode promoCode = this.findById(id)

        promoCode.isOn = !promoCode.isOn

        promoCodeRepository.save(promoCode)
    }

    /**
     * Удаление промокода
     * @param id
     */
    void delete(String id){

        log.debug("Delete promocode by ID [$id]")

        promoCodeRepository.deleteById(id)
    }

    /**
     * Поиск промокода по идентификатору
     * @param id
     * @return
     */
    PromoCode findById(String id){

        log.debug("Find promoCode by ID [$id]")

        PromoCode promoCode = promoCodeRepository.findById(id).orElse(null)

        if(!promoCode){
            log.debug("Not found promoCode by ID [$id]")
            throw new PromoCodeNotFoundException("Not found promoCode by ID [$id]", "promo.code.not.found.exception")
        }

        log.debug("Found promoCode ID [$promoCode.id]")

        return promoCode
    }

    /**
     * Поиск промокодов по пользователю
     * @param userId
     * @return
     */
    List<PromoCode> findAllByUserId(String userId){

        log.debug("Find all promoCodes by user ID [$userId]")

        List<PromoCode> promoCodes = promoCodeRepository.findAllByUserId(userId)

        if(!promoCodes){
            log.debug("Not found any discounts")
            return []
        }

        log.debug("Found promoCodes size [${promoCodes.size()}]")

        return promoCodes
    }

    /**
     * Поиск промокодов по событию
     * @param eventId
     * @return
     */
    Page<PromoCode> findAllByEventId(String eventId, int page, int size){

        log.debug("Find all promoCodes by event ID [$eventId]")

        Page<PromoCode> promoCodes = promoCodeRepository.findAllByEventId(eventId, new PageRequest(page, size))

        if(!promoCodes){
            log.debug("Not found any discounts")
            return []
        }

        log.debug("Found promoCodes size [${promoCodes.size()}]")

        return promoCodes
    }

    /**
     * Поиск промокодов по идентификаторам
     * @param eventId
     * @return
     */
    List<PromoCode> findAllByIds(List<String> ids){

        log.debug("Find all promoCodes by IDs size [${ids.size()}]")

        List<PromoCode> promoCodes = promoCodeRepository.findAllByIdIn(ids)

        if(!promoCodes){
            log.debug("Not found any discounts")
            return []
        }

        log.debug("Found promoCodes size [${promoCodes.size()}]")

        return promoCodes
    }

    /**
     * Поиск промокода по тексту кода
     * @param eventId
     * @return
     */
    PromoCode findByCodeAndEventId(String eventId, String code){

        log.debug("Find promoCode by code [$code] and event ID [$eventId]")

        PromoCode promoCode = promoCodeRepository.findAllByCodeAndEventId(code, eventId)

        if(!promoCode){
            log.debug("Not found promoCode by code [$code]")
            throw new PromoCodeNotFoundException("Not found promoCode by code [$code] and event ID [$eventId]", "promo.code.not.found.exception")
        }

        log.debug("Found promoCode ID [$promoCode.id]")

        return promoCode
    }

    /**
     * Поиск промокода по тексту кода
     * @param eventId
     * @return
     */
    PromoCode findByCodeAndEventIdOrNull(String eventId, String code){

        log.debug("Find promoCode by code [$code] and event ID [$eventId]")

        PromoCode promoCode = promoCodeRepository.findAllByCodeAndEventId(code, eventId)

        if(!promoCode){
            log.debug("Not found promoCode by code [$code]")
            return null
        }

        log.debug("Found promoCode ID [$promoCode.id]")

        return promoCode
    }

    /**
     * Поиск промокода по тексту кода
     * @param eventId
     * @return
     */
    PromoCode findByCodeAndEventIdOrReturnNull(String eventId, String code){

        log.debug("Find promoCode by code [$code] and event ID [$eventId]")

        PromoCode promoCode = promoCodeRepository.findAllByCodeAndEventId(code, eventId)

        if(!promoCode){
            log.debug("Not found promoCode by code [$code]")
            return null
        }

        log.debug("Found promoCode ID [$promoCode.id]")

        return promoCode
    }

    /**
     * Поиск промокода по дате и идентификатору события
     * @param eventId
     * @return
     */
    List<PromoCode> findActiveCodeByEventId(String eventId){

        log.debug("Find promoCode active by event ID [$eventId]")

        Long now = System.currentTimeMillis()

        List<PromoCode> promoCodes = promoCodeRepository.findAllByEventIdAndStartLessThanAndEndGreaterThan(eventId, now, now)

        if(!promoCodes){
            log.debug("Not found promoCode by event ID [$eventId]")
            return []
        }

        log.debug("Found promoCode ID [${promoCodes.size()}]")

        return promoCodes
    }

    /**
     * Сохранение промо кода
     * @param promoCode
     */
    void save(PromoCode promoCode){
        log.debug("Save promocode code: [$promoCode.code]")

        promoCode = promoCodeRepository.save(promoCode)

        log.debug("Saved promocode ID [$promoCode.id]")
    }
}
