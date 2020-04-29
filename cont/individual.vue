<template>
  <div>
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
          :label="$t('contract.sername')"
          v-model="form.sername"
          :error-message="form.sername === '' ? $t('errors.requiredField') : $t('errors.wrongData')"
          v-validate="rules.sername"
        />
      </div>

      <div class="tile__row tile__row--flex-start tile__row--margin-top-x2">

        <basic-float-input
          :label="$t('contract.userName')"
          v-model="form.name"
          :error-message="form.name === '' ? $t('errors.requiredField') : $t('errors.wrongData')"
          v-validate="rules.name"
        />

        <basic-float-input
          class="tile__row--margin-left"
          :label="$t('contract.secondName')"
          v-model="form.patronymic"
          :error-message="form.patronymic === '' ? $t('errors.requiredField') : $t('errors.wrongData')"
          v-validate="rules.patronymic"
        />
      </div>

      <div class="tile__row tile__row--flex-start tile__row--margin-top-x2">

        <basic-float-input
          :label="$t('contract.identifierINN')"
          v-model="form.identifier_inn"
          :error-message="form.identifier_inn === '' ? $t('errors.requiredField') : $t('errors.wrongData')"
          v-validate="rules.identifierINN"
        />
      </div>

    </div>

    <!--Contacts block-->
    <div class="tile--background-white tile__row--paddings-x2 tile__row--margin-top">
      <span class="tile__text tile__text--big24">{{$t('contract.contacts')}}</span>

      <div class="tile__row tile__row--column-direction tile__row--margin-top">
        <basic-float-input
          :label="$t('contract.address') + ' *'"
          v-model="form.address"
          :error-message="form.address === '' ? $t('errors.requiredField') : $t('errors.wrongData')"
          v-validate="rules.address"
        />

        <basic-float-input
          type="tel"
          maxLength="13"
          minLength="13"
          class="tile__row--margin-top-x2"
          :label="$t('contract.phone')"
          v-model="form.phone"
          name="document_phone"
          @input="phoneValidate"
          :error-message="form.phone === '' ? $t('errors.wrongPhone') : $t('errors.requiredField')"
          :validate-rule="rules.phone"
          v-validate="rules.phone"
        />
      </div>
    </div>

<!--    Moderation block-->
    <div v-if="false" class="tile--background-white tile__row--paddings-x2 tile__row--margin-bottom-x2">
      <span class="tile__text tile__text--big24">{{$t('contract.moderation')}}</span>

      <!-- Switch show contact data -->
      <div class="tile__column tile__row--margin-top">
        <input
          type="checkbox"
          class="form__input form__input--checkbox"
          id="input_data_switcher1"
          data-qa="input.org_data_switcher1"
          v-model="form.status === contractStatus.ACTIVE"
          @change="setContractStatus(form.status === contractStatus.ACTIVE.default ? contractStatus.MODERATION_REPEAT.default : contractStatus.ACTIVE.default)"
        />
        <label
          for="input_data_switcher1"
          class="form__label form__label--small form__label--switch"
        >{{$t('contract.activateContract')}}</label>
      </div>
    </div>
  </div>
</template>

<script>
    import {organizationType, contractStatus} from "@/api/contract";

    export default {
      name: "individual",
      data(){
        return {
            organizationTypes: [],
            organizationType,
            contractStatus,
          rules: {
            sername: 'required|min:1|max:80',
            name: 'required|min:1|max:80',
            patronymic: 'required|min:1|max:80',
            identifierINN: 'required|min:1|max:80',
            address: 'required|min:1|max:80',
            phone: 'required|min:1|max:12',
          }
        }
      },
      props: {
        form:{
            type:Object,
            required:true
        }
      },
      mounted(){
          let that = this;
          that.setOrganizationTypes();
      },
      methods: {
          phoneValidate(){
              let that = this;
              that.form.phone = that.form.phone.replace(/[^0-9\+]/g, '')
          },
          setContractStatus(status){
             this.$emit("set-contract-status", status);
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

              that.form.organization_type = that.organizationType.FO.default;
          },
          getOrganizationType(val){
              let that = this;
              that.form.organization_type = val;
          },
      }
    }
</script>

<style lang="scss" scoped>
</style>
