package com.ololoCompany.meetizer.business.service

import com.ololoCompany.meetizer.business.data.domain.Company
import com.ololoCompany.meetizer.business.data.domain.Contract
import com.ololoCompany.meetizer.business.data.domain.ContractStateHistory
import com.ololoCompany.meetizer.business.data.domain.ContractStatus
import com.ololoCompany.meetizer.business.data.domain.MainDocument
import com.ololoCompany.meetizer.business.data.domain.OrganizationType
import com.ololoCompany.meetizer.business.data.domain.ScanDocumentType
import com.ololoCompany.meetizer.business.data.domain.ScanImage
import com.ololoCompany.meetizer.business.data.domain.TaxSystem
import com.ololoCompany.meetizer.business.data.domain.User
import com.ololoCompany.meetizer.business.data.repository.ContractStateHistoryRepository
import com.ololoCompany.meetizer.business.data.service.ContractDataService
import com.ololoCompany.meetizer.business.data.service.ContractStateHistoryDataService
import com.ololoCompany.meetizer.business.model.company.CompanyPublicModel
import com.ololoCompany.meetizer.business.model.contract.ContractRequestDto
import com.ololoCompany.meetizer.business.model.contract.ContractResponseDto
import com.ololoCompany.meetizer.business.model.contract.ContractStateHistoryResponseDto
import com.ololoCompany.meetizer.business.model.contract.ContractsModelResponseDto
import com.ololoCompany.meetizer.business.model.contract.ContractsResponseDto
import com.ololoCompany.meetizer.business.model.contract.FilterContractsModel
import com.ololoCompany.meetizer.business.model.promo.PathRequestDto
import groovy.util.logging.Slf4j
import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Slf4j
@Service
class ContractService {

    @Autowired private ContractDataService contractDataService
    @Autowired private ContractStateHistoryDataService contractStateHistoryDataService
    @Autowired private UserService userService

    @Autowired private CompanyService companyService
    @Autowired private PdfService pdfService

    /**
     * Создание договора
     * @param dto
     * @param userId
     */
    ContractResponseDto create(ContractRequestDto dto, String userId){

        log.info("Create contract user ID [$userId]")

        User user = userService.getUserBymeetizerUserId(userId)

        Company company = companyService.findDomainById(dto.companyId)

        Contract contract = contractDataService.create(dto, userId)
        company.contracts.add(contract)
        companyService.save(company)

        pdfService.convertContractToPdf(contract)

        contractStateHistoryDataService.createHistory(contract, userId)

        return ContractResponseDto.buildFromDomain(contract, user.name)
    }

    /**
     * Редактирование договора
     * @param dto
     * @param userId
     */
    void edit(ContractRequestDto dto, String userId){

        log.info("Edit contract ID [$dto.id]")

        contractDataService.edit(dto, userId)
    }

    /**
     * Поиск по пользователю
     * @param userId
     * @return
     */
    List<ContractResponseDto> findAllByUserId(String userId){

        log.info("Search contract by user ID [${userId}]")

        User user = userService.getUserBymeetizerUserId(userId)

        List<Contract> contracts = contractDataService.findAllByUserId(userId)

        return contracts.collect {return ContractResponseDto.buildFromDomain(it, user.name)}
    }

    /**
     * Поиск по идентификатору
     * @param userId
     * @return
     */
    ContractResponseDto findById(String id){

        log.info("Search contract by ID [${id}]")

        Contract contract = contractDataService.findById(id)

        User user = userService.getUserBymeetizerUserId(contract.userId)

        CompanyPublicModel company = companyService.publicCompanyByIdFullInfo(contract.companyId)

        return ContractResponseDto.buildFromDomainWithCompany(contract, user.name, company)
    }

    ContractsModelResponseDto filterContracts(FilterContractsModel filter, int page, int size){

        log.info("Search contract by filter [${filter}]")

        PageRequest pageRequest = new PageRequest(page, size)

        if(!filter.statuses){
            filter.statuses = ContractStatus.STATUSES_FOR_FILTER
        }

        List<User> users
        if(filter.userName){
            users = userService.findAllUsersByName(filter.userName)
            filter.userIds = users.userId
        }

        List<Company> companies

        ContractsResponseDto responseDto = contractDataService.filterContract(filter, pageRequest)

        users = userService.findAllUsersByUserIds(responseDto.contracts.userId)

        ContractsModelResponseDto modelResponseDto = new ContractsModelResponseDto(
                count: responseDto.count,
                contracts: responseDto.contracts.collect{
                    return ContractResponseDto.buildFromDomain(it, users.find {user -> user.userId == it.userId}.name)
                }
        )

        return modelResponseDto
    }

    /**
     * Сохранение пути к скан копии договора
     * @param userId
     * @param contractId
     * @param imagePath
     * @param type
     */
    void saveImagePath(String userId, String contractId, List<PathRequestDto> pathRequestDtos){

        log.info("Save image pathes contract ID [${contractId}], user ID [${userId}]")

        Contract contract = contractDataService.findById(contractId)

        ScanImage scanImage

        Long now = new Date().time

        pathRequestDtos.each { dto ->
            scanImage = contract.scanImages.find {it.type == dto.type}

            if(scanImage){
                scanImage.createDate = now
                scanImage.name = dto.name
                scanImage.path = dto.path
            } else {
                scanImage = new ScanImage(
                        createDate: now,
                        name: dto.name,
                        type: dto.type,
                        path: dto.path
                )
                contract.scanImages.add(scanImage)
            }
        }

        contractDataService.save(contract)
    }

    /**
     * Перевод скан копии в статус промодерировано
     * @param userId
     * @param contractId
     * @param type
     */
    void moderateScanCopy(String userId, String contractId, ScanDocumentType type, boolean isModerate){

        log.info("Save image path contract ID [${contractId}], user ID [${userId}]")

        Contract contract = contractDataService.findByIdAndUserId(contractId, userId)

        ScanImage scanImage = contract.scanImages.find {it.type == type}

        if(scanImage){
            scanImage.isModerate = isModerate
        }

        contractDataService.save(contract)
    }

    /**
     * Шаблон контракта
     * @param userId
     * @return
     */
    String getContractTemplate(String userId){

        log.info("Get contract template. User ID [${userId}]")

        Contract contract = new Contract(
                organizationType: OrganizationType.TOV,
                organizationName: "__________________",
                taxSystem: TaxSystem.COMMON,
                identificationCode: "__________________",
                bankName: "__________________",
                bankCode: "__________________",
                checkingAccount: "__________________",
                mainOrgDocument: MainDocument.CERTIFICATE,
                managerName: "__________________",
                legalAddress: "__________________",
                legalPhone: "__________________",
                documentAddress: "__________________",
                documentPhone: "__________________",
                number: 0
        )

        byte[] contractPdf = pdfService.convertContractToPdf(contract)

        return Base64.encodeBase64String(contractPdf)
    }

    /**
     * Контракт по
     * @param id
     * @param userId
     * @return
     */
    String getContract(String contractId){

        log.info("Get contract ID [${contractId}]")

        Contract contract = contractDataService.findById(contractId)

        byte[] contractPdf = pdfService.convertContractToPdf(contract)

        return Base64.encodeBase64String(contractPdf)
    }

    /**
     * Поиск истории договора
     * @param contractId
     * @return
     */
    List<ContractStateHistoryResponseDto> findAllHistoryByContractId(String contractId){

        log.info("Get contract history by ID [${contractId}]")

        List<ContractStateHistory> history = contractStateHistoryDataService.findAllByContractId(contractId)

        return history.collect {ContractStateHistoryResponseDto.buildFromDomain(it)}
    }

    /**
     * Смена статуса
     * @param contractId
     * @param status
     */
    void setStatus(String contractId, String userId, ContractStatus status, String reason){

        log.info("Set contract status ID [${contractId}] status [${status}]")

        Contract contract = contractDataService.findById(contractId)
        ContractStatus oldStatus = contract.status
        contract.status = status

        contract.isScanExist = status == ContractStatus.SCAN_ADDED

        if(reason){
            contract.returnReason = reason
        }

        contractDataService.save(contract)

        contractStateHistoryDataService.createHistory(contract, userId, oldStatus, reason)

        log.info("Successfully set contract status ID [${contractId}] status [${status}]")
    }

    /**
     * Включить/Отключить продажу билетов
     * @param contractId
     * @param userId
     */
    void enableDisableTicketSale(String contractId){

        log.info("Enable/disable ticket sale, contract ID [${contractId}]")

        Contract contract = contractDataService.findById(contractId)
        contract.enableTicketSale = !contract.enableTicketSale
        contractDataService.save(contract)

        log.info("Successfully enabled/disabled ticket sale ID [${contractId}]")
    }

    /**
     * Установить показатель получен оригинал
     * @param contractId
     * @param userId
     */
    void receiveOrigin(String contractId, String userId){

        log.info("Receive origin, contract ID [${contractId}]")

        Contract contract = contractDataService.findById(contractId)
        ContractStatus oldStatus = contract.status
        if(!contract.isOriginExist){
            contract.status = ContractStatus.ACTIVE
        }
        contract.isOriginExist = !contract.isOriginExist

        contractDataService.save(contract)

        contractStateHistoryDataService.createHistory(contract, userId, oldStatus)

        log.info("Successfully received origin, contract ID [${contractId}] ")
    }

    /**
     * Включить/Отключить вывод средств
     * @param contractId
     * @param userId
     */
    void enableDisableWithdrawal(String contractId){

        log.info("Enable/disable withdrawal, contract ID [${contractId}]")

        Contract contract = contractDataService.findById(contractId)
        contract.enableWithdrawal = !contract.enableWithdrawal
        contractDataService.save(contract)

        log.info("Successfully enabled/disabled withdrawal ID [${contractId}]")
    }

    /**
     * Получить скан копию контракта
     * @param contractId
     * @param userId
     */
    void receiveScan(String contractId){

        log.info("Receive scan copy of main contract, contract ID [${contractId}]")

        Contract contract = contractDataService.findById(contractId)
        contract.isScanExist = !contract.isScanExist
        contractDataService.save(contract)

        log.info("Successfully received scan copy of main contract  ID [${contractId}]")
    }

    /**
     * Удалить скан документа
     * @param contractId
     * @param userId
     */
    void deleteScan(String contractId, ScanDocumentType type){

        log.info("Delete scan copy, type [${type}] contract ID [${contractId}]")

        Contract contract = contractDataService.findById(contractId)
        contract.scanImages = contract.scanImages.findAll {it.type != type}
        contractDataService.save(contract)

        log.info("Successfully deleted scan copy type [${type}] contract  ID [${contractId}]")
    }
}
