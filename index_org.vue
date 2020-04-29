<template>
  <div class="tile__row tile__row--align-start">
    <div class="leftblock tile__row tile__row--align-start">
      <div v-preloader="{ showLoader, text: $t('events.loading')}" style="width: 692px;">

        <form class="company__form" enctype="multipart/form-data" :model="form">

          <!-- Block main info -->
          <div class="tile--background-white tile__row--paddings-x2 tile__column">
            <div class="tile__column" style="min-width: 444px">

              <div class="tile__row tile__row--column-direction">
                <div class="tile__row tile__row--flex-start tile__row--align-start tile__row--margin-bottom">
                  <div>
                    <h2 class="tile__text tile__text--vertical-top">{{$t(companyState !== 'EDIT' && companyState !== 'VIEW'
                      ? 'orgz.creation' : 'common.organization')}}</h2>
                    <span class="tile__text tile__text--tips" style="font-size: 16px;">{{$t('orgz.tips.create')}}</span>
                  </div>
                </div>

                <!--Title-->
                <basic-float-input
                  :label="$t(`orgz.titlePlaceholder`)+` *`"
                  v-model="form.name"
                  name="titleCompany"
                  :is-words-limited="true"
                  maxlength="120"
                  :error-message="$t('errors.requiredField')"
                  v-validate="rules.title"
                  :validate-rule="rules.title"
                  :ref-name="'titleCompany'"
                  ref="titleCompany"
                  :validate-event="formFields.titleCompany && formFields.titleCompany.validated ? formFields.titleCompany.valid
                  : !!(formFields.titleCompany && !formFields.titleCompany.validated)"
                />

                <!-- Description -->
                <basic-float-input
                  class="tile__row--margin-top-x2"
                  :textArea="true"
                  :refName="'textArea'"
                  :label="$t(`orgz.description`)"
                  :is-words-limited="true"
                  :error-message="$t('errors.requiredField')"
                  v-model="form.description"
                  name="title"
                  maxlength="250"
                  v-validate="rules.description"
                />
              </div>
            </div>

            <!-- Upload Cover -->
            <div class="tile__row tile__row--column-direction tile__row--margin-left-one-half">
              <div class="tile__text tile__text--margin-bottom text-cover">
                {{$t('inputName.pictureLabel')}}
              </div>
              <image-input
                :image-src="form.logo_path ? form.logo_path : organization_logo_path"
                @imagePath="imagePath"
                :small-icons="true"
                ref="coverInput"
                class="tile__cover--company"
                style="min-width: 160px;    max-height: 90px;"
              ></image-input>
              <div class="tile__text ile__text--lighten text-cover">
                {{$t('orgz.tips.cover')}}
              </div>
            </div>
          </div>

          <!-- Block Contact DATA -->
          <div class="tile--background-white tile__row--paddings-x2 tile__row--margin-top-half">
            <div class="tile__row--padding-right tile__row--align-start">
              <h2 class="form__header">{{$t('orgz.headers.contacts')}}</h2>
            </div>

            <div class="tile__row tile__row--grow">
              <basic-float-input
                class="tile__row--margin-right-x2"
                :label="$t(`orgz.email`)+` *`"
                v-model="form.email"
                name="email"
                :error-message="form.email !== '' ? $t('errors.wrongEmail') : $t('errors.requiredField')"
                :validate-rule="rules.email"
                v-validate="rules.email"
                :validate-event="formFields.email && formFields.email.validated ? formFields.email.valid
                  : !!(formFields.email && !formFields.email.validated)"
              />

              <!-- Phone number  -->
              <basic-float-input
                type="tel"
                :label="$t(`orgz.phone`)+` *`"
                :validate-rule="rules.phone"
                v-validate="rules.phone"
                maxLength="13"
                minLength="13"
                @input="phoneValidate"
                v-model="form.phone"
                name="phoneCompany"
                :error-message="form.phone !== '' ? $t('errors.wrongPhone') : $t('errors.requiredField')"
                :validate-event="formFields.phoneCompany && formFields.phoneCompany.validated ? formFields.phoneCompany.valid
                  : !!(formFields.phoneCompany && !formFields.phoneCompany.validated)"
              />
            </div>

            <!-- Switch show contact data -->
            <div class="tile__column tile__row--margin-top-x2">
              <input
                type="checkbox"
                class="form__input form__input--checkbox"
                id="input_data_switcher1"
                data-qa="input.org_data_switcher1"
                v-model="form.is_enable_contact_details"
              />
              <label
                for="input_data_switcher1"
                class="form__label form__label--small form__label--switch tile__row--margin-left-half"
              >{{$t('orgz.tips.show')}}</label>
            </div>
          </div>

          <!-- Block URLs -->
          <div class="tile--background-white tile__row--paddings-x2 tile__row--margin-top-half"
               v-if="!isView || (isView && (form.url || form.url_instagram || form.url_facebook || form.url_linkedin))">
            <h2 class="form__header">{{$t('orgz.headers.links')}}</h2>

            <!-- website -->
            <basic-float-input
              class="tile__row--margin-top-half"
              :label="$t(`inputName.site`)"
              v-model="form.url"
              name="site"
            />

            <div class="tile__row tile__row--margin-top-x2 tile__row--padding-left-half"
                 v-if="!isView || (isView && form.url_instagram)">
              <!-- link Instagram -->
              <svg-icon width="24px" height="24px" name="instagram" :original="true"
                        style="margin-right: var(--indent)"></svg-icon>

              <basic-float-input
                :label="'instagram'"
                v-model="form.url_instagram"
                name="url_instagram"
              />
            </div>

            <div class="tile__row tile__row--margin-top-x2 tile__row--padding-left-half"
                 v-if="!isView ||  (isView && form.url_facebook)">
              <!-- link facebook -->
              <svg-icon width="24px" height="24px" name="facebook" color="gray"
                        style="margin-right: 16px"></svg-icon>

              <basic-float-input
                :label="'facebook'"
                v-model="form.url_facebook"
                name="url_facebook"
              />
            </div>

            <div class="tile__row tile__row--margin-top-x2 tile__row--padding-left-half"
                 v-if="!isView ||  (isView && form.url_linkedin)">
              <!-- link linkedin -->
              <svg-icon width="24px" height="24px" name="linkedin" color="gray"
                        style="margin-right: 16px;"></svg-icon>

              <basic-float-input
                :label="'linkedin'"
                v-model="form.url_linkedin"
                name="url_linkedin"
              />
            </div>
          </div>

          <!-- Выбор статуса Организации (продавец билетов, бесплатные ивенты, тест) -->
          <div class="tile--background-white tile__row--paddings-x2 tile__row--margin-top-half tile__row--margin-bottom-x2">
            <div class="tile__row--paddings tile--background-main">
              <h2 class="form__header">{{$t('orgz.headers.state')}}</h2>

              <div class="form__group--checkbox-padding" v-for="el in stateTypes">
                <input
                  type="radio"
                  v-model="form.state"
                  :id="'checkbox_'+el"
                  :checked="el === form.state"
                  :value="el"
                  class="form__input form__input--checkbox"
                  @change="setVariant(el)"
                />
                <label @click="setVariant(el)"
                       :for="'checkbox_'+el"
                       class="form__label form__label--circle"
                >{{$t('orgz.states.' + el.toLowerCase())}}</label>
              </div>

            </div>
          </div>

          <transition>
            <footer-buttons :isSidebarOpened = isSidebarOpened>
              <div class="tile__row tile__row--flex-end" slot="content">
                <button data-qa="button.company_create" class="btn btn--secondary btn--size-middle"
                        type="button" @click.prevent="onSave" :disabled="!isValidPhone">
                  {{$t('common.save')}}
                </button>
                <button v-if="!$route.params.id" type="button" data-qa="button.company_next"
                        class="btn btn--size-middle" @click.prevent="onNext"
                        :disabled="!isValidPhone"
                >
                  {{$t('common.next')}}
                </button>
              </div>
            </footer-buttons>
          </transition>
        </form>

        <draft-modal
          :show-modal="showModalSave"
          @leaveWithoutSave="leaveWithoutSave"
          @submit="onSave"
          @close="showModalSave = !showModalSave"
        />
      </div>

      <!--Contracts-->
      <div v-if="currentCompany.id" class="tile__row tile__row--column-direction tile__row--paddings tile__row--background-white tile__row--margin-left-one-half tile__row--align-start">
        <span class="tile__text tile__text--medium18Bold">{{$t('contract.contracts')}}</span>

        <template v-if="typeof currentCompany.contracts !== 'undefined'">
          <div v-for="contract in currentCompany.contracts"  class="tile__row tile__row--align-start tile__row--padding-top-half cursor" @click="editContract(contract)">
            <svg-icon class="icon" name="contract"/>
            <div class="tile__row tile__row--column-direction tile__row--align-start">
              <span class="tile__text tile__text--small16">{{contract.organization_name}}</span>
              <span class="tile__text tile__text--small12 tile__row--margin-top-div4">
            {{organizationType[contract.organization_type].name[$i18n.locale] + " / "}}
                <span class="tile__text tile__text--small12Orange"
                  :class="[contractStatus.MODERATION_NEW, contractStatus.MODERATION_REPEAT].includes(contractStatus[contract.status]) ? 'tile__text--small12Orange' :
            contractStatus[contract.status] === contractStatus.ACTIVE ? 'tile__text--small12Green' :
            [contractStatus.NEW, contractStatus.CREATED].includes(contractStatus[contract.status]) ? 'tile__text--small12Red' : 'tile__text--small12'">
              {{contractStatus[contract.status].name[$i18n.locale]}}
                </span>
              </span>
            </div>
          </div>
        </template>

        <div class="horizontal-br tile__row--margin-top"></div>
        <div class="tile__row tile__row--align-start cursor tile__row--margin-top" @click="createContract">
          <svg-icon class="icon icon-color" color="#2979FF" name="plus"/>
          <div class="tile__row tile__row--column-direction tile__row--align-start">
            <span class="tile__text tile__text--small16Blue">{{$t('contract.createContract')}}</span>
            <span class="tile__text tile__text--small12 tile__row--margin-top-div4">{{$t('contract.startSellingTickets')}}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
    import request from '@/utils/request';
    import Img from '../../../components/common/uploadImage.vue';
    import preloader from "../../../directive/preloader/index.js";
    import {Message} from 'element-ui';
    import {getCompany} from '@/api/company';
    import FooterButtons from '@/components/common/FooterButtons'
    import {contractStatus, organizationType} from '@/api/contract';

    export default {
        name: "public-info",
        components: {'image-input': Img, FooterButtons},
        directives: {preloader},
        props: ['org'],
        data() {

            return {
                organization_logo_path: "",
                isValidPhone: true,
                emailConfirmProposition: false,
                stateTypes: ['SELLER', 'FREE', 'TESTING'],
                showLoader: false,
                onNextButtonPressed: false,
                name: '',
                form: {
                    id: '',
                    name: '',
                    description: '',
                    email: '',
                    phone: '',
                    url: '',
                    url_instagram: '',
                    url_facebook: '',
                    url_linkedin: '',
                    logo_path: '',
                    is_enable_contact_details: false,
                    state: 'SELLER',
                    confirm_email: false
                },

                isEdit: false,
                isTicketSeller: false,
                bindProps: {inputOptions: {showDialCode: true}},

                rules: {
                    title: 'required|max:120',
                    description: "max:250",
                    email: "required|email",
                    phone: "required|min:13||max:13",
                },

                //Draft modal
                currentCompany: {},
                leave_path: '',
                showModalSave: false,
                onSubmitClick: false,
                isAutoLeave: false,
                warningListener: '',
                f5Listener: '',
                contractStatus,
                organizationType
            }
        },
        computed: {
            companyState() {
                return this.$route.params.companyState;
            },
            isSidebarOpened() {
                return this.$store.getters.isSidebarOpened;
            },
            isView() {
                let that = this;
                let isView = that.companyState === 'VIEW';
                return isView
            },
        },
        mounted(){
            let that = this;
            if(that.$route.params.id){
                that.getCompany();
            }
        },
        created() {
            window.addEventListener('beforeunload', this.onReload);
            window.addEventListener('keydown', this.pressF5)
        },
        beforeDestroy() {
            window.removeEventListener('beforeunload', this.onReload);
            window.removeEventListener('keydown', this.pressF5);
        },
        beforeRouteLeave(to, from, next) {
            if (this.isCompareContent() || this.onSubmitClick || this.isAutoLeave) {
                next()
            } else {
                this.showModalSave = true;
                this.leave_path = to.path;
            }
        },
        methods: {
            phoneValidate(){
                let that = this;
                that.form.phone = that.form.phone.replace(/[^0-9\+]/g, '')
            },
            editContract(contract){
                this.$router.push({path: `/${this.currentCompany.id}/contracts/edit/${contract.id}`})
            },
            createContract(){
                this.$router.push({path: `/${this.currentCompany.id}/contracts/create`})
            },
            onNext(){
                let that = this;
                that.onNextButtonPressed = true;
                if(that.form.state === 'SELLER'){
                    that.onSave()
                } else {
                    that.$router.push('/event/create')
                }
            },
            setVariant(variant){
                let that = this;
                that.form.state = variant;
            },
            onSave(){
                let that = this;
                that.onSubmitClick = true;
                if(that.$route.params.id){
                    that.editCompany();
                } else {
                    that.createCompany();
                }
            },
            getCompany(){
                let that = this;
                that.showLoader = true;

                getCompany(this.$route.params.id).then((response) => {
                    that.form = {...response.data};
                    that.currentCompany = {...response.data};
                    that.form.description = response.data.description ? response.data.description : '';
                    that.currentCompany.description = response.data.description ? response.data.description : '';
                    that.showLoader = false;
                }).catch(error => {
                    if (error.response.data.code === 1008) this.$router.push('/404');
                });
            },
            pressF5(event) {
                if ((event.which || event.keyCode) === 116 || (event.which || event.keyCode) === 82) {
                    this.onReload();
                }
            },
            onReload(event) {
                let that = this;
                if (typeof event !== 'undefined' && !that.isFormEmpty()) {
                    event.returnValue = "some text"
                }
            },
            leaveWithoutSave() {
                this.isAutoLeave = true;
                this.$router.push(this.leave_path);
            },
            toEdit() {
                let that = this;

                this.$router.push({
                    path: `/organizations/edit/${this.$route.params.id}`,
                    params: {company: this.$route.params.id, companyState: 'EDIT'}
                });
            },

            validatePhone(event) {
                this.isValidPhone = event.isValid
            },

            confirmModal() {
                this.$router.push({name: 'View_Organization'});
                this.emailConfirmProposition = false;
            },

            imagePath(imgPath) {

                this.organization_logo_path = imgPath;
                this.form.logo_path = imgPath;
            },

            onCancel() {
                this.$router.push('/');
            },

            handleValidationErrorBasic() {
                const firstField = Object.keys(this.errors.collect())[0];
                this.$refs[`${firstField}Input`].focus();
            },

            trimStringFields(field) {
                this.form[field] = this.form[field] ? this.form[field].trim() : '';
            },
            createCompany() {
                this.showLoader = true;
                if (!this.isValidPhone) {
                    this.showModalSave = false;
                    this.showLoader = false;
                    this.form.phone = "error";
                    return;
                }
                this.$validator.validate().then(valid => {
                    if (!valid) {
                        this.showModalSave = false;
                        this.showLoader = false;
                        this.$message.error(this.$t('errors.notValid'));
                        return this.handleValidationErrorBasic();
                    } else {

                        request({
                            url: '/api/company',
                            method: 'post',
                            data: {
                                ...this.form,
                                phone: this.form.phone.replace(new RegExp(" ", "g"), ""),
                            },
                        })
                            .then((response) => {
                                if (response.status === 200) {
                                    this.$store.dispatch('setCompanyExist', true);
                                    this.emailConfirmProposition = true;
                                    this.currentCompany.id = response.data.id;

                                    if(this.form.state === 'SELLER' && this.onNextButtonPressed){
                                        this.createContract();
                                    } else if(this.onNextButtonPressed){
                                        this.$router.push('/event/create');
                                    } else {
                                        this.$router.push({name: 'View_Organization'});
                                    }
                                }
                                this.showLoader = false;
                            })
                            .catch(function (error) {
                                this.showLoader = false;
                            });
                    }
                })
            },

            isCompareContent() {
               let that = this;

                if(that.$route.params.id){
                    return that.form.name === that.currentCompany.name &&
                        that.form.description === that.currentCompany.description &&
                        that.form.email === that.currentCompany.email &&
                        that.form.url === that.currentCompany.url &&
                        that.form.logo_path === that.currentCompany.logo_path &&
                        that.form.is_enable_contact_details === that.currentCompany.is_enable_contact_details &&
                        that.form.url_instagram === that.currentCompany.url_instagram &&
                        that.form.url_facebook === that.currentCompany.url_facebook &&
                        that.form.url_linkedin === that.currentCompany.url_linkedin &&
                        that.form.state === that.currentCompany.state
                } else {
                    return that.form.name === '' &&
                        that.form.description === '' &&
                        that.form.email === '' &&
                        that.form.url === '' &&
                        that.form.logo_path === '' &&
                        that.form.is_enable_contact_details === false &&
                        that.form.url_instagram === '' &&
                        that.form.url_facebook === '' &&
                        that.form.url_linkedin === '' &&
                        that.form.state === 'SELLER'
                }
            },
            editCompany() {
                let that = this;
                that.$validator.validate().then(valid => {
                    if (!valid) {
                        this.showModalSave = false;
                        this.showLoader = false;
                        this.$message.error(this.$t('errors.notValid'));
                        return this.handleValidationErrorBasic();
                    } else {
                        request({
                            url: '/api/company',
                            method: 'put',
                            data: {
                                id: that.$route.params.id,
                                ...that.form,
                            },
                        }).then((response) => {
                            Message({
                                showClose: true,
                                message: that.$t('common.savedChanges'),
                                type: 'info',
                                duration: 5 * 1000,
                            });
                            if (response.status === 200) {
                                that.$router.push({name: 'View_Organization'});
                            }
                        }).catch((error) => {

                        });
                    }
                })
            },
            // resendMail() {
            //     request({
            //         url: `/api/company/${this.$route.params.id}/resend-confirm`,
            //         method: 'get',
            //     }).then((response) => {
            //         if (response.status === 200) {
            //             // this.emailResend = true;
            //             this.emailConfirmProposition = true
            //         }
            //         // company.cannot.send.email
            //     }).catch(error => {
            //         if (error.response && error.response.data && error.response.data.code === 1008) this.$router.push('/404');
            //     });
            // },
        }
    }
</script>


<style lang="scss">
  /* for vue tel input  */
  @import "../../../assets/styles/global/variables";

  .vue-tel-input:focus {
    border-bottom: 2px solid #2979FF;
  }

  .dropdown-item {
    font-family: $font-base;
    font-size: 14px;
    color: #636E69;
  }
</style>

<style lang="scss" scoped>
  @import "../../../assets/styles/global/variables";

  .svg-icon {
    width: 16px;
    height: 16px;
    margin-right: calc(var(--indent) / 2 );
    vertical-align: text-top;
  }

  .icon {
    width: 24px;
    height: 24px;
    &-color {
      color: $ui_blue;
    }
  }

  .cursor {
    cursor: pointer;
  }

  .horizontal-br {
    width: 100%;
    height: 2px;
    background: $main-background;
  }

  .Image-input {
    margin-bottom: 16px;
    min-width: 212px;
    max-width: 212px;
    height: 119px;
  }

  .error {
    border-color: red;
  }

  .form__input.vue-tel-input {
    border-radius: 0;
    border: none;
    border-bottom: 1px solid #219653;
  }

  .vue-tel-input > input::placeholder {
    color: $text_tertiary;
  }

  .company__form {
    width: 100%;
    max-width: 1080px;
  }

  .tile__cover--company {
    max-width: 160px;
    background: $main-background;
    border: 1px solid $text_tertiary;
    box-sizing: border-box;
    display: flex;
    align-items: center;
  }

  .text-cover {
    font-weight: 300;
    font-size: 12px;
    line-height: 120%;
    letter-spacing: 0.5px;
    color: #344235;
  }

  .limit-option {
    margin-top: 2px;
    font-family: $font-base;
    font-style: normal;
    font-weight: 300;
    font-size: 10px;
    line-height: 120%;
    text-align: right;
    letter-spacing: 0.5px;
    color: #636E69;
    display: flex;
    justify-content: flex-end;
  }
</style>
