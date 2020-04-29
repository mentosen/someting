package com.ololoCompany.eventssion.business.data.service

import com.ololoCompany.eventssion.business.data.domain.comCont
import com.ololoCompany.eventssion.business.data.domain.comContStatus
import com.ololoCompany.eventssion.business.data.domain.Permissions
import com.ololoCompany.eventssion.business.data.domain.UserPermissions
import com.ololoCompany.eventssion.business.data.repository.comContRepository
import com.ololoCompany.eventssion.business.data.repository.comContRepositoryCustom
import com.ololoCompany.eventssion.business.exception.comContNotFound
import com.ololoCompany.eventssion.business.exception.WrongFilterException
import com.ololoCompany.eventssion.business.model.comCont.comContRequestDto
import com.ololoCompany.eventssion.business.model.comCont.comContsResponseDto
import com.ololoCompany.eventssion.business.model.comCont.FiltercomContsModel
import com.ololoCompany.eventssion.business.service.CompanyService
import com.ololoCompany.eventssion.business.service.UserPermissionService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Slf4j
@Service
class comContDataService {

    @Autowired private comContRepository comContRepository
    @Autowired private comContRepositoryCustom comContRepositoryCustom

    @Autowired private UserPermissionService userPermissionService

    /**
     * Создание договора
     * @param dto
     */
    comCont create(comContRequestDto dto, String userId) {

        log.debug("Create comCont. User ID: [${userId}]. comOrg ID: [${dto.companyId}]")

        comCont comCont = comContRepository.save(new comCont(
                comOrgType: dto.comOrgType,
                comOrgName: dto.comOrgName,
                taxSystem: dto.taxSystem,
                identificationCode: dto.identificationCode,
                bankName: dto.bankName,
                bankCode: dto.bankCode,
                checkingAccount: dto.checkingAccount,
                mainOrgDocument: dto.mainOrgDocument,
                managerName: dto.managerName,
                status: comContStatus.NEW,
                legalAddress: dto.legalAddress,
                legalPhone: dto.legalPhone,
                documentAddress: dto.documentAddress,
                documentPhone: dto.documentPhone,
                sername: dto.sername,
                name: dto.name,
                patronymic: dto.patronymic,
                identifierInn: dto.identifierInn,
                address: dto.address,
                phone: dto.phone,
                companyId: dto.companyId,
                userId:userId
        ))

        log.debug("Successfully saved. comCont ID [${comCont.id}]")

        return comCont
    }

    /**
     * Изменение договора
     * @param dto
     */
    void edit(comContRequestDto dto, String userId) {

        log.debug("Edit comCont. User ID: [${userId}]. comCont ID: [${dto.id}]")

        comCont comCont = this.findById(dto.id)

        this.checkUserRole(comCont, userId)

        comCont.comOrgType = dto.comOrgType
        comCont.comOrgName = dto.comOrgName
        comCont.taxSystem = dto.taxSystem
        comCont.identificationCode = dto.identificationCode
        comCont.bankName = dto.bankName
        comCont.bankCode = dto.bankCode
        comCont.checkingAccount = dto.checkingAccount
        comCont.mainOrgDocument = dto.mainOrgDocument
        comCont.managerName = dto.managerName
        comCont.legalAddress = dto.legalAddress
        comCont.legalPhone = dto.legalPhone
        comCont.documentAddress = dto.documentAddress
        comCont.documentPhone = dto.documentPhone

        comCont.sername = dto.sername
        comCont.name = dto.name
        comCont.patronymic = dto.patronymic
        comCont.identifierInn = dto.identifierInn
        comCont.address = dto.address
        comCont.phone = dto.phone

        comCont.companyId = dto.companyId
        comCont.userId = userId

        comCont = comContRepository.save(comCont)

        log.debug("Successfully saved. comCont ID [${comCont.id}]")
    }

    private void checkUserRole(comCont comCont, String userId){

        UserPermissions roleUsers = userPermissionService.getUserPermission(userId)

        if (userId != comCont.userId && !roleUsers.permissions.contains(Permissions.MODERATION_comCont.toString())) {

            log.info("[${userId}] User doesn't have enough rights to edit event")
            throw new Exception("User doesn't have enough rights to edit event")
        }
    }

    /**
     * Поиск договора по пользователю и идентификатору
     * @param userId
     * @return
     */
    comCont findByIdAndUserId(String comContId, String userId){

        log.debug("Find comCont by ID [${comContId}] and user ID [${userId}]")

        comCont comCont = comContRepository.findByIdAndUserId(comContId, userId)

        if(!comCont){
            log.error("Not found any comConts by user ID [${userId}]")
            throw new comContNotFound("Not found any comConts by user ID [${userId}]", "comCont.not.found.exception")
        }

        log.debug("Found comCont ID [${comCont.id}]")

        return comCont
    }

    /**
     * Поиск договора по идентификатору
     * @param userId
     * @return
     */
    comCont findById(String comContId){

        log.debug("Find comCont by ID [${comContId}]")

        comCont comCont = comContRepository.findById(comContId).orElse(null)

        if(!comCont){
            log.error("Not found comCont ID [${comContId}]")
            throw new comContNotFound("Not found comCont ID [${comContId}]", "comCont.not.found.exception")
        }

        log.debug("Found comCont ID [${comCont.id}]")

        return comCont
    }

    /**
     * Поиск договора по пользователю
     * @param userId
     * @return
     */
    List<comCont> findAllByUserId(String userId){

        log.debug("Find all comConts by user ID [${userId}]")

        List<comCont> comConts = comContRepository.findAllByUserId(userId)

        if(!comConts){
            log.error("Not found any comConts by user ID [${userId}]")
            return []
        }

        log.debug("Found comCont ID [${comConts.size()}]")

        return comConts
    }

    /**
     * Сохранение договора
     * @param comCont
     */
    void save(comCont comCont){

        comCont = comContRepository.save(comCont)

        log.debug("comCont successfully saved, ID [$comCont.id]")
    }

    /**
     * Фильтрация договоров
     * @param filterModel
     * @return
     */
    comContsResponseDto filtercomCont(FiltercomContsModel filterModel, Pageable pageable){

        log.debug("Filter comConts [${filterModel}]. Page [${pageable.getPageNumber()}]. Size [${pageable.getPageSize()}]")

        comContsResponseDto dto = comContRepositoryCustom.filtercomConts(filterModel, pageable)

        if(!dto){
            log.error("No filter comConts [${filterModel}]. Page [${pageable.getPageNumber()}]. Size [${pageable.getPageSize()}]")
            throw new WrongFilterException("No filter comConts [${filterModel}]. Page [${pageable.getPageNumber()}]. Size [${pageable.getPageSize()}]", 'wrong.filter.exception')
        }

        log.debug("Found comConts size [${dto.count}]")

        return dto
    }
}
