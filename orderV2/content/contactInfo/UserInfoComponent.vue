<template>
    <div class="tile__column" :style="isMobile ? 'width: 100%' : 'width: 50%'">
        <float-label
                class="tile__row--margin-top-origin"
                :dispatch="true"
                :read-only="false"
                :fixed="currentUser.name !== '' || typeof currentUser.name !== 'undefined'"
                :label="$t('auth.name')"
                :is-error="!!errors.first('name')"
        >
            <input type="text"
                   name="name"
                   class="form__input"
                   ref="name"
                   autofocus="false"
                   :placeholder="$t('auth.name')"
                   v-model="currentUser.name"
                   v-validate="rules.name"
                   data-qa="input.user_name"
                   @input="setUserInfo"
            >
            </input>
            <span v-if="!!errors.first('name')" class="form__error is-invalid">{{$t('error.requiredField')}}</span>
        </float-label>
        <float-label
                class="tile__row--margin-top-origin"
                :dispatch="true"
                :read-only="false"
                :fixed="currentUser.email !== ''"
                :label="$t('auth.mail')"
                :is-error="!!errors.first('email')"
        >
            <input type="text"
                   name="email"
                   class="form__input"
                   ref="email"
                   autofocus="false"
                   v-model="currentUser.email"
                   placeholder="example@gmail.com"
                   v-validate="rules.email"
                   data-qa="input.user_mail"
                   @input="setUserInfo"
            >
            </input>
            <span v-if="!!errors.first('email')" class="form__error is-invalid">{{currentUser.email !== '' && typeof currentUser.email !== 'undefined' ? $t('error.emailWrong') : $t('error.requiredField')}}</span>
        </float-label>
        <float-label
                class="tile__row--margin-top-origin"
                :dispatch="true"
                :read-only="false"
                :fixed="currentUser.phone !== ''"
                :label="$t('tickets.phoneNumber')"
                :is-error="!!errors.first('phone')"
        >
            <vue-tel-input
                    v-model="currentUser.phone"
                    :preferredCountries="['ua', 'ru', 'us']"
                    name="phone"
                    id="input_phone"
                    placeholder="+380 XXX XX XX"
                    :enabledCountryCode = "false"
                    :disabledFormatting="true"
                    :enabledFlags = "true"
                    defaultCountry="ua"
                    autocomplete='off'
                    ref="phone_input"
                    v-validate="rules.phone"
                    :valid-characters-only="true"
                    :maxLen="13"
                    style="border: none;"
                    :style="isMobile ? 'font-size: 16px' : ''"
                    v-bind="bindProps"
                    data-qa="input.auth_phone"
                    @input="setUserInfo"
            >
            </vue-tel-input>

        </float-label>

        <button class="btn btn-v2-link font font--description" data-qa="button.logout" @click="logout">
            {{$t("auth.logout")}}
        </button>
    </div>
</template>

<script>
    import {ticketFlowState} from "../../../../api/ticket";
    import VueTelInput from 'vue-tel-input';

    export default {
        name: "UserInfoComponent",
        components:{VueTelInput},
        data(){
            return {
                currentUser: {
                    name: "",
                    email: "",
                    phone: ""
                },
                currentUserStatus: "",
                currentOrder: {},
                bindProps: {
                    dropdownOptions: {
                        disabledDialCode: false
                    },
                    inputOptions: {
                        showDialCode: true
                    }
                },
                rules: {
                    phone: "min:13|max:13",
                    name: {
                        required: true,
                    },
                    email: {
                        required: true,
                        email: true
                    },
                }
            }
        },
        created(){

            let that = this;
            that.$store.commit('SET_CURRENT_FLOW_STATE', ticketFlowState.USER_INFO);
            that.currentOrder = that.$store.getters.ORDER;
            that.$store.commit('SET_LANGUAGE', this.$store.getters.LANGUAGE);
            that.setNextState();

            that.currentUser = this.$store.getters.getUserInfo;
            if(that.currentUser.status === 'CONFIRM_PROFILE' || that.currentUser.name === 'John Doe') {
                that.currentUser.name = undefined;
                that.currentUserStatus = that.currentUser.status;
            }

            that.$store.dispatch("GetUserInfo").then(() => {
                that.currentUser = this.$store.getters.getUserInfo;
                if(that.currentUserStatus === 'CONFIRM_PROFILE' || that.currentUser.name === 'John Doe'){
                    that.currentUser.name = undefined;
                }
                that.setUserInfo();
            });
            this.$emit('close-loader');
        },
        computed: {
            isMobile() {
                return this.$store.getters.isMobile;
            }
        },
        methods: {
            phoneValidate() {
                if(typeof this.currentUser.phone !== 'undefined' && this.currentUser.phone !== '' && this.currentUser.phone.substr(0, 1) !== "+"){
                    this.currentUser.phone = "+" + this.currentUser.phone;
                    this.currentUser.phone = this.currentUser.phone.replace(/\s/g, '');
                }

                return /^\+\d{12}$/.test(this.currentUser.phone)
            },
            emailValidate() {
                const re = /^[a-z0-9!#$%&'*+/=?^_'{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_'{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$/i;
                if(re.test(this.currentUser.email)){
                    return true
                }
                return re.test(this.currentUser.email);
            },
            logout() {
                let that = this;
                // //update in analytic session
                // that.$bus.$emit("update-analytic");
               this.$emit('logout');
            },
            setNextState(){
                let that = this;
                let ticketsWithSurvey = that.currentOrder.tickets.filter(it => it.add_survey === true);

                if(ticketsWithSurvey && ticketsWithSurvey.length > 0){
                    that.$store.commit('SET_FLOW_STATE', ticketFlowState.SURVEY);
                } else {
                    that.$store.commit('SET_FLOW_STATE', ticketFlowState.PAYMENT);
                }
            },
            setUserInfo(){
                let that = this;

                if(that.currentUser.name && typeof that.currentUser.name !== 'undefined' && that.currentUser.name !== ''){
                    that.currentUser.name = that.currentUser.name.trim();
                }

                if(typeof that.currentUser.email !== 'undefined' && that.currentUser.email !== ''){
                    that.currentUser.email = that.currentUser.email.trim();
                }

                if(that.phoneValidate() && that.currentUser.name !== '' && that.emailValidate()){

                    that.currentOrder.user_name = that.currentUser.name;
                    that.currentOrder.user_email = that.currentUser.email;
                    that.currentOrder.user_phone = that.currentUser.phone;
                    that.currentOrder.user = {
                      name: that.currentUser.name,
                      email: that.currentUser.email,
                      phone: that.currentUser.phone
                    };
                    that.currentOrder.tickets.forEach(it => {
                        it.required_fields.name = that.currentUser.name;
                        it.required_fields.email = that.currentUser.email;
                        it.required_fields.phone = that.currentUser.phone;
                    });
                    that.$store.commit('SET_ORDER', that.currentOrder);
                    that.currentUser.status = that.currentUserStatus;
                    that.$store.commit("SET_USER_INFO", that.currentUser);

                    this.$emit('enable-continue-button')
                } else {
                    this.$emit('disable-continue-button')
                }
            }
        }
    }
</script>

<style lang="scss">

    .btn-v2-link {
        width: 50%;
        align-self: center;
        background: transparent;
        text-transform: none;
        color: #2979ff;

        &:hover:not([disabled]) {
            color: darken(#2979ff, 15%);
        }
    }

    .vti__dropdown {
        outline: none;
        background: transparent;
        z-index: 10;
    }
    .sign-in-phone__input {
        background: transparent;
    }

    .vue-tel-input{
        border: none!important;
        border-bottom: 1px solid #BCC0BC!important;

        &:focus-within{
            box-shadow: none!important;
        }
    }

    ul{
        z-index: 10!important;
    }

    .sign-in-phone {
        width: 100%;
        display: flex;
        flex-direction: column;

        &__select {
            background: transparent;
            border: none;
            position: relative;
            font-size: 18px;
            &:focus, &:focus-within {
                border: none;
                outline: none;
                box-shadow: none;
            }
            &::after {
                content: '';
                width: 100%;
                height: 1px;
                background: blue;
                position: absolute;
                bottom: 0;
            }
        }
    }

</style>