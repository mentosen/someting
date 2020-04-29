<template>
  <div class="leftblock tile__row tile__row--align-start" v-preloader="{ showLoader, text: $t('events.loading')}">
    <div class="tile__row tile__row--flex-start tile__row--align-normal">

      <form class="form" enctype="multipart/form-data" :model="form" v-if="form.organization_type !== organizationType.FO.default">

        <!--Requsites block-->
        <div class="tile--background-white tile__row--paddings-x2">
          <div class="tile__row tile__row--column-direction tile__row--align-start">
            <span class="tile__text tile__text--big24">{{$t('contract.requisites')}}</span>
            <span class="tile__text tile__text--small16Light tile__row--margin-top-half">{{$t('contract.requisitesDescription')}}</span>
          </div>

          <div class="tile__row tile__row--flex-start tile__row--margin-top">

            <select-component
              style="width: 40%"
              v-if="organizationTypes !== []"
              :title="$t('contract.organizationType')"
              :items="organizationTypes"
              :default-value="form.organization_type"
              v-model="form.organization_type"
              @get-value="getOrganizationType"
            />

            <basic-float-input
              class="tile__row--margin-left"
              :label="$t('contract.organizationName')"
              v-model="form.organization_name"
              name="organization_name"
              :error-message="form.organization_name === '' ? $t('errors.requiredField') : $t('errors.wrongData')"
              :validate-rule="rules.organizationName"
              v-validate="rules.organizationName"
              :validate-event="formFields.organization_name && formFields.organization_name.validated ? formFields.organization_name.valid
                  : !!(formFields.organization_name && !formFields.organization_name.validated)"
            />
          </div>

          <div class="tile__row tile__row--margin-top-x2">
            <select-component
              v-if="taxSystems !== []"
              :items="taxSystems"
              :title="$t('contract.taxSystem')"
              v-model="form.tax_system"
              :default-value="form.tax_system"
              @get-value="getTaxType"
            />

            <basic-float-input
              type="number"
              class="tile__row--margin-left"
              :label="$t('contract.identifier')"
              name="identification_code"
              v-model="form.identification_code"
              :error-message="form.identification_code === '' ? $t('errors.requiredField') : $t('errors.wrongData')"
              :validate-rule="rules.identifier"
              v-validate="rules.identifier"
              :validate-event="formFields.identification_code && formFields.identification_code.validated ? formFields.identification_code.valid
                  : !!(formFields.identification_code && !formFields.identification_code.validated)"
            />
          </div>

          <div class="tile__row tile__row--flex-start tile__row--margin-top-x2">

            <basic-float-input
              :label="$t('contract.bankName')"
              v-model="form.bank_name"
              name="bank_name"
              :error-message="form.bank_name === '' ? $t('errors.requiredField') : $t('errors.wrongData')"
              :validate-rule="rules.bankName"
              v-validate="rules.bankName"
              :validate-event="formFields.bank_name && formFields.bank_name.validated ? formFields.bank_name.valid
                  : !!(formFields.bank_name && !formFields.bank_name.validated)"
            />

            <basic-float-input
              type="number"
              style="width: 40%"
              class="tile__row--margin-left"
              :label="$t('contract.bankCode')"
              name="bank_code"
              v-model="form.bank_code"
              :error-message="form.bank_code === '' ? $t('errors.requiredField') : $t('errors.wrongData')"
              :validate-rule="rules.bankCode"
              v-validate="rules.bankCode"
              :validate-event="formFields.bank_code && formFields.bank_code.validated ? formFields.bank_code.valid
                  : !!(formFields.bank_code && !formFields.bank_code.validated)"
            />

          </div>

          <div class="tile__row tile__row--margin-top-x2">
            <basic-float-input
              type="number"
              :label="$t('contract.checkingAccount')"
              v-model="form.checking_account"
              name="checking_account"
              :error-message="form.checking_account === '' ? $t('errors.requiredField') : $t('errors.wrongData')"
              :validate-rule="rules.checkingAccount"
              v-validate="rules.checkingAccount"
              :validate-event="formFields.checking_account && formFields.checking_account.validated ? formFields.checking_account.valid
                  : !!(formFields.checking_account && !formFields.checking_account.validated)"
            />
          </div>
        </div>

        <!--Manager block-->
        <div class="tile--background-white tile__row--paddings-x2 tile__row--margin-top">
          <span class="tile__text tile__text--big24">{{$t('contract.manager')}}</span>
          <div class="tile__row tile__row--column-direction tile__row--margin-top">

            <select-component
              v-if="mainOrgDocuments !== []"
              :title="$t('contract.organizationType')"
              :items="mainOrgDocuments"
              :default-value="form.main_org_document"
              name="main_org_document"
              v-model="form.main_org_document"
              @get-value="getMainOrgDocument"
            />

            <basic-float-input
              class="tile__row--margin-top-x2"
              :label="$t('contract.managerName')"
              v-model="form.manager_name"
              name="manager_name"
              :error-message="form.manager_name === '' ? $t('errors.requiredField') : $t('errors.wrongData')"
              :validate-rule="rules.managerName"
              v-validate="rules.managerName"
              :validate-event="formFields.manager_name && formFields.manager_name.validated ? formFields.manager_name.valid
                  : !!(formFields.manager_name && !formFields.manager_name.validated)"
            />

          </div>
        </div>

        <!--Legal block-->
        <div class="tile--background-white tile__row--paddings-x2 tile__row--margin-top">
          <span class="tile__text tile__text--big24">{{$t('contract.legalAddress')}}</span>

          <div class="tile__row tile__row--column-direction tile__row--margin-top">
            <basic-float-input
              :label="$t('contract.legalAddress') + ' *'"
              v-model="form.legal_address"
              name="legal_address"
              :error-message="form.legal_address === '' ? $t('errors.requiredField') : $t('errors.wrongData')"
              :validate-rule="rules.legalAddress"
              v-validate="rules.legalAddress"
              :validate-event="formFields.legal_address && formFields.legal_address.validated ? formFields.legal_address.valid
                  : !!(formFields.legal_address && !formFields.legal_address.validated)"
            />

            <basic-float-input
              type="tel"
              maxLength="13"
              minLength="13"
              class="tile__row--margin-top-x2"
              :label="$t('contract.phone')"
              v-model="form.legal_phone"
              @input="phoneLegalValidate"
              name="legal_phone"
              :error-message="form.legal_phone !== '' ? $t('errors.wrongPhone') : $t('errors.requiredField')"
              :validate-rule="rules.legalPhone"
              v-validate="rules.legalPhone"
              :validate-event="formFields.legal_phone && formFields.legal_phone.validated ? formFields.legal_phone.valid
                  : !!(formFields.legal_phone && !formFields.legal_phone.validated)"
            />
          </div>
        </div>

        <!--Address block-->
        <div class="tile--background-white tile__row--paddings-x2 tile__row--margin-top"
             :class="isModeration ? 'tile__row--margin-bottom' : 'tile__row--margin-bottom-x2'">
          <span class="tile__text tile__text--big24">{{$t('contract.documentAddress')}}</span>

          <div class="tile__row tile__row--column-direction tile__row--margin-top">
            <basic-float-input
              :label="$t('contract.documentAddress') + ' *'"
              v-model="form.document_address"
              name="document_address"
              :error-message="form.document_address === '' ? $t('errors.requiredField') : $t('errors.wrongData')"
              :validate-rule="rules.documentAddress"
              v-validate="rules.documentAddress"
              :validate-event="formFields.document_address && formFields.document_address.validated ? formFields.document_address.valid
                  : !!(formFields.document_address && !formFields.document_address.validated)"
            />

            <basic-float-input
              type="tel"
              maxLength="13"
              minLength="13"
              class="tile__row--margin-top-x2"
              :label="$t('contract.phone')"
              v-model="form.document_phone"
              name="document_phone"
              @input="phoneDocumentValidate"
              :error-message="form.document_phone !== '' ? $t('errors.wrongPhone') : $t('errors.requiredField')"
              :validate-rule="rules.documentPhone"
              v-validate="rules.documentPhone"
              :validate-event="formFields.document_phone && formFields.document_phone.validated ? formFields.document_phone.valid
                  : !!(formFields.document_phone && !formFields.document_phone.validated)"
            />
          </div>
        </div>

<!--        Moderation block-->
        <div v-if="isModeration" class="tile--background-white tile__row--paddings-x2 tile__row--margin-bottom-x2">
          <span class="tile__text tile__text--big24">{{$t('contract.moderation')}}</span>

          <!-- Switch show contact data -->
          <div class="tile__column tile__row--margin-top">
            <input
              type="checkbox"
              class="form__input form__input--checkbox"
              id="input_data_switcher"
              data-qa="input.contract_moderation_switcher"
              v-model="form.status === contractStatus.ACTIVE"
              @change="setContractStatus(form.status === contractStatus.ACTIVE.default ? contractStatus.MODERATION_REPEAT.default : contractStatus.ACTIVE.default)"
            />
            <label
              for="input_data_switcher"
              class="form__label form__label--small form__label--switch tile__row--margin-left"
            >{{$t('contract.activateContract')}}</label>
          </div>
        </div>
      </form>

      <form class="form" enctype="multipart/form-data" :model="form" v-if="form.organization_type === organizationType.FO.default">
        <individual
          :form="form"
          @set-contract-status="setContractStatus"
        ></individual>
      </form>

      <transition>
        <footer-buttons :isSidebarOpened = isSidebarOpened>
          <div class="tile__row tile__row--flex-end" slot="content">
            <button data-qa="button.create_or_edit_contract" class="btn btn--secondary btn--size-middle"
                    type="button" @click.prevent="onSave" :disabled="isDisabledOnSave">
              {{$t('common.save')}}
            </button>
            <button type="button" data-qa="button.send_contract"
                    class="btn btn--size-middle" @click.prevent="onSend" :disabled="isDisabledOnSend">
              {{$t('common.send')}}
            </button>
          </div>
        </footer-buttons>
      </transition>
    </div>

    <div class="tile__row--margin-left-one-half" style="width: 32%;">
      <upload-block
        :id="form.id ? form.id : ''"
        :isFO="form.organization_type === organizationType.FO.default"
        :images="uploadImages"
        @add-image="addImageToSave"
        @remove="removeImage"/>

<!--      Status block-->
      <div class="tile__row--paddings tile__row--margin-top-one-per-half tile--background-white">
        <span class="tile__text tile__text--medium18Bold">{{$t("contract.contract")}}</span>

        <div class="tile__row tile__row--column-direction tile__row--align-start tile__row--margin-top">
          <div class="tile__row tile__row--flex-start">
            <svg-icon name="contract"/>
            <span class="tile__text tile__text--small16Light">{{$t("contract.statusTitle")}}</span>
          </div>
          <span class="tile__text tile__row--margin-left-one-half tile__row--margin-top-div4"
                :class="[contractStatus.MODERATION_NEW, contractStatus.MODERATION_REPEAT].includes(contractStatus[form.status]) ? 'tile__text--small14BoldOrange' :
            contractStatus[form.status] === contractStatus.ACTIVE ? 'tile__text--small14Green' :
            [contractStatus.NEW, contractStatus.CREATED].includes(contractStatus[form.status]) ? 'tile__text--small14Red' : 'tile__text--small14'">
              {{contractStatus[form.status].name[$i18n.locale]}}
            </span>
        </div>

        <div v-if="contractStatus[form.status] !== contractStatus.ACTIVE"
             class="tile__row tile__row--align-start tile__row--margin-top">
          <svg-icon name="info"/>
          <span class="tile__text tile__text--small14">
            {{ [contractStatus.NEW, contractStatus.CREATED].includes(contractStatus[form.status]) ? $t("contract.statusInfo.draft") : $t("contract.statusInfo.moderation")}}
          </span>
        </div>

        <div v-if="form.organization_type !== organizationType.FO.default" class="horizontal-br tile__row--margin-top"></div>

        <div v-if="form.organization_type !== organizationType.FO.default" class="tile__row tile__row--margin-top cursor" @click="showContractTemplate">
          <svg-icon style="width: 32px; height: 32px" color="#FF7F6E" name="file-pdf"/>
          <div class="tile__row tile__row--column-direction tile__row--align-start">
            <span class="tile__text tile__text--small14Bold">{{$t("contract.contractExample")}}</span>
            <span class="tile__text tile__text--small12 tile__row--margin-top-div4">{{$t("contract.show")}}</span>
          </div>
        </div>

<!--        Download contract block-->
        <div v-if="isModeration" class="tile__row tile__row--margin-top cursor" @click="downloadFile">
          <svg-icon style="width: 32px; height: 32px" name="file-download"/>
          <div class="tile__row tile__row--column-direction tile__row--align-start">
            <span class="tile__text tile__text--small14">{{$t("contract.downloadContract")}}</span>
            <span class="tile__text tile__text--small12 tile__row--margin-top-div4">{{$t("contract.format")}}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
    import {
        allScanDocumentTypes,
        createContract,
        editContract,
        getContract,
        getContractTemplate,
        saveImagePath,
        scanDocumentType,
        setStatus,
        organizationType,
        contractStatus,
        taxSystem,
        mainDocument,
        deleteScan
    } from "@/api/contract";
    import {saveFiles, deleteFiles} from "@/api/media";

    import uploadBlock from "./uploadBlock";
    import individual from "./individual";
    import request from '@/utils/request';
    import preloader from "../../../directive/preloader/index.js";
    import FooterButtons from '@/components/common/FooterButtons'

    export default {
        name: "contract",
        components: {uploadBlock, individual, preloader, FooterButtons},
        directives: {preloader},
        props: ['contract'],
        data() {
            return {
                organizationId: this.$route.params.id,
                form: {
                    id: null,
                    organization_type: '',
                    organization_name: '',
                    tax_system: '',
                    identification_code: '',
                    bank_name: '',
                    bank_code: '',
                    checking_account: '',
                    main_org_document: '',
                    manager_name: '',
                    legal_address: '',
                    legal_phone: '',
                    document_address: '',
                    document_phone: '',
                    sername: '',
                    name: '',
                    patronymic: '',
                    identifier_inn: '',
                    address:'',
                    phone:'',
                    return_reason: '',
                    status: 'NEW'
                },
                onSendButtonPressed: false,
                isDisabledOnSave: false,
                isDisabledOnSend: false,
                showLoader: false,
                isModeration: false,
                // isModeration: true,
                organizationTypes: [],
                taxSystems: [],
                mainOrgDocuments: [],
                imagesToSave: [],
                uploadImages: [],



                // uploadDocumentType: [],
                isSaveBlock: false,
                isEditBlock: true,
                isScanAddBlock: false,
                isActiveBlock: false,
                isScanActiveBlock: false,
                isUploadBlock: false,
                isOnModerationBlock: false,
                isShowEditBlock: false,
                isRemoveBorder: false,
                isUploadContract: false,
                isUploadDisable: false,
                isIndividual: false,
                isUploadMessage: false,
                disableSaveButton: false,
                rules: {
                    organizationType: 'required',
                    organizationName: 'required|min:1|max:80',
                    taxSystem: 'required',
                    identifier: 'required|min:1|max:12',
                    bankName: 'required|min:1|max:80',
                    bankCode: 'required|min:1|max:6',
                    checkingAccount: 'required|min:1|max:20',
                    mainDocument: 'required|min:1|max:80',
                    managerName: 'required|min:1|max:80',
                    legalAddress: 'required|min:1|max:80',
                    legalPhone: 'required|min:13|max:13',
                    documentAddress: 'required|min:1|max:80',
                    documentPhone: 'required|min:13|max:13'
                },
                scanDocumentType,
                allScanDocumentTypes,
                organizationType,
                taxSystem,
                mainDocument,
                contractStatus
            }
        },

        computed: {
            isSidebarOpened() {
                return this.$store.getters.isSidebarOpened;
            },
        },
        mounted() {
            let that = this;
            if (that.$route.params.contract) {
                that.getContract();
                that.isModeration = that.$route.params.isModeration;
            } else {
                that.isEditBlock = false;
                that.isSaveBlock = true;

                that.uploadImages = [];
            }
            that.setOrganizationTypes();
            that.setTaxSystems();
            that.setMainOrgDocuments();
        },
        methods: {
            phoneDocumentValidate(){
                let that = this;
                that.form.document_phone = that.form.document_phone.replace(/[^0-9\+]/g, '')
            },
            phoneLegalValidate(){
                let that = this;
                that.form.legal_phone = that.form.legal_phone.replace(/[^0-9\+]/g, '')
            },
            removeImage(name, type){
                let that = this;

                that.imagesToSave = that.imagesToSave.filter(file => file.name !== name);

                if(that.form.id !== null){
                    deleteScan(that.form.id, type).then(response => {

                        that.uploadImages = that.uploadImages.filter(it => it.type !== type)
                    })
                }
            },
            addImageToSave(fileOfBlob){
                let that = this;
                that.imagesToSave.push(fileOfBlob);
            },
            saveImageBatch(id) {
                let that = this;

                let signature = this.generateUUID();
                const formData = new FormData();
                that.imagesToSave.forEach((image) => {
                    formData.append("files", image.file, image.file.name);
                });

                saveFiles('CONTRACT', signature, formData, id).then(response => {
                    let path = Object.values(response.data)[0];
                    // that.clearInput(type.toLowerCase());
                    that.saveImagePath(response.data);

                    that.disableSaveButton = false;
                    that.showLoader = false;
                })
            },

            saveImagePath(data) {

                let that = this;
                let pathObjects = [];
                let type = '';
                let name = '';

                Object.entries(data).forEach((entry) => {
                    type = entry[0].substr(0, entry[0].lastIndexOf("."));
                    name = that.imagesToSave.filter(it => it.file.name === entry[0])[0].fileName;
                    pathObjects.push({
                        name: name,
                        path: entry[1],
                        type: type
                    })
                });

                saveImagePath(that.form.id, pathObjects).then(response => {
                    console.log("Image path successfully saved");
                    // this.addDocumentType(type);

                    if(that.onSendButtonPressed){
                        that.$router.push('/event/create');
                    } else {
                        that.$router.push('/organizations');
                    }
                })
            },
            onSend(){
                let that = this;
                that.onSendButtonPressed = true;
                that.isDisabledOnSend = true;
                that.showLoader = true;

                that.onSave();
            },
            onSave(){
                let that = this;
                that.isDisabledOnSave = true;
                that.showLoader = true;

                if(that.$route.params.contract){
                    that.edit();
                } else {
                    that.create();
                }
            },
            setOrganizationTypes() {
                let that = this;

                that.organizationTypes = Object.values(that.organizationType).map(item => {
                    return {
                        value: item.default,
                        name: item.name[that.$i18n.locale],
                        description: ''
                    }
                });

                that.form.organization_type = that.organizationType.TOV.default;
            },
            getOrganizationType(val){
               let that = this;
               that.form.organization_type = val;
            },
            setTaxSystems() {
                let that = this;

                that.taxSystems = Object.values(that.taxSystem).map(item => {
                    return {
                        value: item.default,
                        name: item.name[that.$i18n.locale],
                        description: ''
                    }
                });

                that.form.tax_system = that.taxSystem.COMMON.default;
            },
            getTaxType(val){
               let that = this;
               that.form.tax_system = val;
            },
            setMainOrgDocuments() {
                let that = this;

                that.mainOrgDocuments = Object.values(that.mainDocument).map(item => {
                    return {
                        value: item.default,
                        name: item.name[that.$i18n.locale],
                        description: ''
                    }
                });

                that.form.main_org_document = that.mainDocument.STATUTE.default;
            },
            getMainOrgDocument(val){
               let that = this;
               that.form.main_org_document = val;
            },
            getContract() {
                let that = this;
                request({
                    url: '../api/contract/' + this.$route.params.contract,
                    method: 'get',
                }).then((response) => {
                    that.form = {...response.data};

                    that.isRemoveBorder = true;

                    if (['NEW', 'RETURNED'].includes(that.form.status)) {
                        that.isShowEditBlock = true;
                        that.isUploadDisable = false;
                    }

                    that.isUploadBlock = ['SCAN_ADDED', 'CREATED', 'ACTIVE'].includes(this.form.status);

                    if (['SCAN_ADDED', 'ACTIVE'].includes(this.form.status)) {
                        that.isScanActiveBlock = true;
                        that.isActiveBlock = that.form.status === 'ACTIVE';
                        that.isScanAddBlock = that.form.status === 'SCAN_ADD';
                    }
                    if (response.data.scan_images) {
                        that.uploadImages = response.data.scan_images;

                        // that.uploadDocumentType = that.uploadImages.map(item => item.type);
                        // that.isUploadMessage = that.uploadImages.length === that.allScanDocumentTypes.WITHOUT_MAIN.length
                    } else {
                        that.uploadImages = []
                    }
                }).catch(error => {
                    if (error.response.data.code === 1008) this.$router.push('/404');
                });
            },

            create() {
                let that = this;

                that.$validator.validate().then(valid => {
                    if (!valid) {
                        this.showLoader = false;
                        that.isDisabledOnSend = false;
                        that.isDisabledOnSave = false;
                        that.$message.error(that.$t('errors.notValid'));
                        return this.handleValidationErrorBasic();
                    } else {
                        let data = {
                            company_id: that.organizationId,
                            ...that.form,
                        };
                        createContract(data).then(response => {
                            that.form.id = response.data.id;

                            if(that.imagesToSave.length !== 0 ){
                                that.saveImageBatch(that.form.id);
                            } else if(that.onSendButtonPressed){
                                that.$router.push('/event/create');
                            } else {
                                that.$router.push('/organizations');
                            }
                        })
                    }
                });
            },

            edit() {
                let that = this;
                this.$validator.validate().then(valid => {
                    if (!valid) {
                        this.showLoader = false;
                        that.isDisabledOnSend = false;
                        that.isDisabledOnSave = false;
                        this.$message.error(this.$t('errors.notValid'));
                        return this.handleValidationErrorBasic();
                    } else {
                        let data = {
                            id: that.$route.params.contract,
                            company_id: this.organizationId,
                            ...this.form,
                        };

                        editContract(data).then(response => {
                            if(that.imagesToSave.length !== 0 ){
                                that.saveImageBatch(that.$route.params.contract);
                            } else {
                                that.$router.push('/organizations');
                            }
                        })
                    }
                });
            },

            setContractStatus(value) {
                let status;
                this.form.status === 'RETURNED' ? status = 'MODERATION_REPEAT' : status = value;

                setStatus(this.form.id, status).then(response => {
                    this.form.status = status;
                })
            },

            handleValidationErrorBasic() {
                const firstField = Object.keys(this.errors.collect())[0];
                this.$refs[`${firstField}Input`].focus();
                this.showLoader = false;
            },

            uploadFile(event) {

                let that = this;

                let reader = new FileReader();

                if (event.target.files && event.target.files[0]) {
                    let idxDot = event.target.files[0].name.lastIndexOf(".") + 1;
                    let extFile = event.target.files[0].name.substr(idxDot, event.target.files[0].length).toLowerCase();
                    if (extFile === "jpg" || extFile === "jpeg" || extFile === "pdf") {
                        let file_size = (event.target.files[0].size / 1024 / 1024);// in MB

                        if (file_size > 5) {
                            alert(this.$t('errors.image-size'));
                        } else {
                            reader.readAsDataURL(event.target.files[0]);
                            reader.onload = function (e) {
                                let fileBlob = that.dataURItoBlob(reader.result);
                                that.saveImage(fileBlob, extFile)
                            };
                        }
                    } else {
                        alert(this.$t('errors.image-format'));
                    }
                }
            },

            saveImage(data, extension) {
                let signature = this.generateUUID();
                let fileName = 'document.' + extension;
                const formData = new FormData();
                let fileOfBlob = new File([data], fileName);
                formData.append("files", fileOfBlob);

                saveImage('CONTRACT', signature, formData, this.form.id).then(response => {
                    let path = Object.values(response.data)[0];

                    console.log("Image successfully loaded. Path: " + path);

                    this.saveImagePath(path);
                })
            },

            dataURItoBlob(dataURI) {
                let byteString = atob(dataURI.split(',')[1]);
                let mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];

                let ab = new ArrayBuffer(byteString.length);
                let dw = new DataView(ab);

                for (let i = 0; i < byteString.length; i++) {
                    dw.setUint8(i, byteString.charCodeAt(i));
                }
                return new Blob([ab], {type: mimeString});
            },

            downloadFile() {
                let that = this;
                getContract(that.contract.id).then((response) => {

                    const link = document.createElement('a');
                    link.href = 'data:application/pdf;base64,' + response.data;
                    link.setAttribute('download', 'contract.pdf');
                    document.body.appendChild(link);
                    link.click();
                })
            },

            generateUUID() {
                return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                    let r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
                    return v.toString(16);
                });
            },

            useLegalAddress() {
                let documentAddressInput = this.$refs['input_documentAddress'];
                let documentPhoneInput = this.$refs['input_documentPhone'];

                documentAddressInput.value = this.form.document_address = this.form.legal_address;
                documentPhoneInput.value = this.form.document_phone = this.form.legal_phone;

                this.inputFocus('documentAddress');
                this.inputFocus('documentPhone');
            },

            showContractTemplate() {
                this.showLoader = true;
                getContractTemplate().then((response) => {

                    let pdfWindow = window.open("");
                    pdfWindow.document.body.style.margin = '0';
                    pdfWindow.document.body.style.width = '100%';
                    pdfWindow.document.body.style.height = '100%';
                    pdfWindow.document.addEventListener('contextmenu', event => event.preventDefault());

                    let obj = this.prepareObjForOpen(response.data);
                    pdfWindow.document.body.appendChild(obj)

                    this.showLoader = false;
                })
            },

            disableRightClick(evt) {
                if (evt.button === 2) {
                    console.log("ololo");
                    return false;
                }
            },

            dataURLtoFile(dataurl, filename) {
                let arr = dataurl.split(','), mime = arr[0].match(/:(.*?);/)[1],
                    bstr = atob(arr[1]), n = bstr.length, u8arr = new Uint8Array(n);
                while (n--) {
                    u8arr[n] = bstr.charCodeAt(n);
                }
                return new File([u8arr], filename, {type: mime});
            },

            prepareObjForOpen(data) {
                let file = this.dataURLtoFile('data:application/pdf;base64,' + data, 'contract.pdf');

                let obj = document.createElement('embed');
                obj.onmousedown = this.disableRightClick;
                obj.style.width = '100%';
                obj.style.height = '100%';
                const url = window.URL.createObjectURL(file);
                obj.src = url + '#toolbar=0&navpanes=1&pagemode=thumbs';

                return obj
            },

            // addDocumentType(value) {
            //     let uploadType = this.uploadDocumentType.filter(item => item === value)[0];
            //     if (!uploadType) {
            //         this.uploadDocumentType.push(value);
            //     }
            //
            //     this.isUploadMessage = this.uploadDocumentType.length === this.allScanDocumentTypes.WITHOUT_MAIN.length;
            // },
            clearFileInput(ref) {
                this.$refs[ref].value = "";
            }
        }
    }
</script>

<style lang="scss" scoped>
  @import '../../../assets/styles/global/variables';

  .svg-icon {
    width: 16px;
    height: 16px;
    margin-right: calc(var(--indent) / 2 );
    vertical-align: text-top;
  }

  .horizontal-br {
    width: 100%;
    height: 2px;
    background: $main-background;
  }

  .cursor {
    cursor: pointer;
  }



  .gray-text {
    color: $icons;
    font-weight: 500;
  }

  .font {
    font-weight: normal;
    line-height: 120%;

    &--title {
      font-size: 24px;
      color: $text_primary;
    }

    &--description {
      font-weight: 300;
      font-size: 12px;
      letter-spacing: 0.5px;
      color: $icons;
    }

    &--legal {
      font-size: 16px;
      line-height: 19px;
      letter-spacing: 0.02em;
      color: $text_paragraph_title;
    }

    &--requisites {
      color: $icons;
    }

    &--label {
      font-weight: 500;
      font-size: 18px;
      color: $icons;
    }

    &--created {
      font-size: 16px;
      letter-spacing: 0.5px;
      color: $icons;
    }
  }

  .title {
    margin-bottom: calc(var(--indent) / 2);
  }

  .legal {
    margin-left: calc(var(--indent) * 4.1);
  }

  .block {
    margin-top: calc(var(--indent) * 1.5);
    /*background-color: #dfeaeb;*/
    background-color: $white;
    padding: var(--indent) var(--indent) calc(var(--indent) * 2) var(--indent);
    max-height: 500px;

    &--requisites {
      padding-right: calc(var(--indent) * 4.3);
    }

    &--no-margin-top {
      margin-top: 0;
    }
  }

  .icon {
    margin: 0 calc(var(--indent) * 1.5) 0 calc(var(--indent) / 2);
  }

  .focused {
    transform: translateY(-125%);
    font-size: .75em;
  }

  .form-label {
    position: absolute;
    top: 10px;
    color: $icons;
    z-index: 1;
    transition: transform 150ms ease-out, font-size 150ms ease-out;
  }

  .input-block {
    position: relative;
    width: 100%;
    padding: 0 var(--indent) 0 calc(var(--indent) / 2);
  }

  .error {
    position: absolute;
    top: 45px;
    left: 9px;
    z-index: 1;
  }

  .switch-block {
    justify-content: flex-end;
    max-width: unset;
  }

  .switch-el {
    padding-right: 50px;
    padding-left: 0;
  }

  .switch-el::before {
    right: 0;
    left: unset;
  }

  .switch-el::after {
    right: 18px;
    left: unset;
  }

  .button-block {
    background-color: $card;
    justify-content: space-between;
    /*margin-top: calc(var(--indent) * 1.5);*/
  }

  .dropdown {
    width: 94%;
    padding: calc(var(--indent) / 2) 0;
    background-color: $white;
    position: absolute;
    margin: auto;
    display: flex;
    align-items: flex-start;
    flex-direction: column;
    border-radius: 5px;
    z-index: 2;
    box-sizing: border-box;
  }

  .dropdown-btn {
    border-radius: 0;
    margin-left: 0;
  }

  .width {
    width: 100%;
  }

  input[type=number]::-webkit-inner-spin-button,
  input[type=number]::-webkit-outer-spin-button {
    -webkit-appearance: none;
    margin: 0;
  }

  .button-margin {
    margin-left: calc(var(--indent) * 1.5);
  }

  .btn-margin-top {
    margin-top: calc(var(--indent) * 1.5);
  }

  .span-margin {
    margin-left: var(--indent);
  }

  .remove_border {
    border-bottom: none;
  }

  .reset-img {
    width: auto;
    position: relative;
    display: flex;
    color: #6d757a;
    font-size: 12px;
    cursor: pointer;
    overflow: hidden;
  }

  .reset-img input {
    position: absolute;
    width: 100%;
    height: 100%;
    right: 0;
    top: 0;
    opacity: 0;
    cursor: pointer
  }

  .radio-btn {
    padding: 10px 0 10px 10px;
    width: 35%
  }

  .icon-arrow {
    padding-top: 13px;
    border-bottom: 1px solid $text_tertiary;

    &--red-border {
      border-bottom: 1px solid #FF7F6E;
    }
  }

  .pop-up-footer {
    z-index: 2;
    position: sticky;
    bottom: 0;
    display: flex;
  }
</style>
