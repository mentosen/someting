<template>
    <div v-if="!isCodeBlock" class="tile__row tile__row--column-direction tile__row--padding-top">
        <div class="tile__row tile__row--column-direction tile__row--margin-top-origin" :style="isMobile ? 'width: 100%' : 'width: 320px'">
            <span class="font font--subtitle">{{$t('auth.loginAuth')}}</span>

            <button :style="isMobile ? 'width: 100%' : 'width: 320px'" class="btn-v2  btn-v2--auth font font--subtitle tile__row--margin-top-half" @click="signInGoogle">
                <img class="icon" src="../../../../assets/images/social/ic_google_color.svg"/>Google</button>
            <fb-signin-button :style="isMobile ? 'width: 100%' : 'width: 320px'" class="btn-v2  btn-v2--auth font font--subtitle"
                    :params="fbSignInParams"
                    @success="onSignInSuccess"
                    @error="onSignInError">
                <img class="icon" src="../../../../assets/images/social/ic_facebook_color.svg"/>Facebook</fb-signin-button>

            <span class="font font--subtitle tile__row--margin-bottom">{{ $t("auth.separator") }}</span>

            <div class="tile__row
            tile__row--column-direction
            tile__row--align-start
            tile__row--align-self-center
            tile__row--margin-right-half">

                <float-label
                        :dispatch="true"
                        :read-only="false"
                        :fixed="true"
                        :label="$t('tickets.phoneNumber')"
                >
                    <vue-tel-input
                            v-model="authPhone.phone"
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
                            @focus="setCursor"
                    >
                    </vue-tel-input>
                    <span v-if="isError" class="form__error">{{$t('error.operatorCode')}}</span>
                </float-label>
            </div>

            <button v-show="isPhoneValid"
                    ref="sendSmsCode"
                    style="width: 320px;"
                    class="btn-v2 btn-v2--colored-black tile__row--margin-top-half"
                    data-qa="button.send_sms_code"
                    @click.prevent="sendSmsCode">{{ $t("actions.sms") }}</button>
        </div>
    </div>
    <code-component
            v-else
            :error="isSmsCodeError"
            @resend-sms="sendSmsCode"
            @change-number="isCodeBlock = false"
            @accept-code="signInPhone"
    ></code-component>
</template>

<script>
    import axios from "axios";
    import {setToken, getToken} from "@/utils/auth";
    import {eventssionLink} from "../../../../utils/constants";
    import VueTelInput from 'vue-tel-input'
    import moment from "moment";
    import request from '@/utils/request';
    import {serverApiVersion} from '@/utils/constants';
    import CodeComponent from "./CodeComponent";
    import {ticketFlowState} from "../../../../api/ticket";

    export default {
        name: "AuthComponent",
        components: {CodeComponent,VueTelInput},
        data() {
            return {
                fbSignInParams: {
                    scope: "email",
                    return_scopes: true,
                    redirect_uri: ""
                },
                authPhone: {
                    show: false,
                    phone: "",
                    disableBtn: true,
                    inputPhoneShow: true,
                    smsCode: "",
                    error: false
                },
                bindProps: {
                    dropdownOptions: {
                        disabledDialCode: false
                    },
                    inputOptions: {
                        showDialCode: true
                    }
                },
                rules: {
                    phone: "required|min:13||max:13",
                },
                isCodeBlock: false,
                isSmsCodeError: false,
                isError: false,
                newUser: {},
                currentUser: {}
            }
        },
        mounted(){
            let that = this;

            this.$emit('close-loader');
            that.$store.commit('SET_CURRENT_FLOW_STATE', ticketFlowState.AUTH);
            this.$store.dispatch("GetUserInfo").then(() => {
                this.currentUser = this.$store.getters.getUserInfo;
            });
            that.disableContinueButton();
        },
        computed: {
            isPhoneValid() {
                if(!this.phoneValidate()){
                    this.isError = false;
                    return false
                } else {
                    return true
                }
            },
            isMobile() {
                return this.$store.getters.isMobile;
            },
        },
        watch: {
            isPhoneValid(){
                this.$nextTick(() => {
                    let element = this.$refs.sendSmsCode;
                    let container = this.$refs.container;
                    container.scrollTop = element.scrollTo
                })
            }
        },
        methods: {
            setCursor(){
                console.log('ololo')
            },
            phoneValidate() {
                if(this.authPhone.phone !== '' && this.authPhone.phone.substr(0, 1) !== "+"){
                    this.authPhone.phone = "+" + this.authPhone.phone;
                }

                this.authPhone.phone = this.authPhone.phone.replace(/\s/g, '');

                return /^\+\d{12}$/.test(this.authPhone.phone)
            },
            emailValidate() {
                const re = /^[a-z0-9!#$%&'*+/=?^_'{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_'{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$/i;
                return this.currentUser.email ? re.test(this.currentUser.email) : re.test(this.email);
            },
            setUserInfo() {
                this.$emit("show-survey");
                this.formDisabled = true;
                // //update in analytic session
                // this.$bus.$emit("update-analytic");
            },

            updateAnalyticAndShowForm: function () {

                this.formDisabled = false
                //update in analytic session
                this.$bus.$emit("update-analytic");
            },

            sendSmsCode: function () {

                // //update in analytic session
                // this.$bus.$emit("update-analytic");

                let that = this;
                if (window.dataLayer !== undefined) {
                    window.dataLayer.push({
                        'event': "event-to-ga",
                        'eventCategory': 'auth',
                        'eventAction': 'try-login',
                        'eventLabel': 'phone'
                    });
                }
                axios
                    .post(
                        "/api/uaa/send-verification-code?for_login=true&phone=" +
                        encodeURIComponent(
                            that.authPhone.phone.replace(new RegExp(" ", "g"), "")
                        ),
                        {
                            headers: {
                                "X-Client-Api-Version": serverApiVersion,
                                "X-Platform-Type": "WEB"
                            }
                        }
                    )
                    // eslint-disable-next-line
                    .then(response => {

                        that.$store.commit('SET_PHONE', that.authPhone.phone);
                        that.authPhone.inputPhoneShow = false;
                        that.isCodeBlock = true;
                    })
                    .catch(error => {
                        that.isError = true;
                        if (window.dataLayer !== undefined) {
                            window.dataLayer.push({
                                'event': "event-to-ga",
                                'eventCategory': 'auth',
                                'eventAction': 'error-login',
                                'eventLabel': 'phone-sms'
                            });
                        }
                        // eslint-disable-next-line
                        console.log(error);
                    });
            },
            signInPhone: function (code) {

                let that = this;
                //update in analytic session
                that.$bus.$emit("update-analytic");

                let userLang = navigator.language || navigator.userLanguage;
                let timeZone = that.$moment.tz.guess();
                axios
                    .post(
                        "/api/uaa/login",
                        {
                            code: code,
                            language: userLang,
                            time_zone: timeZone,
                            type: "NATIVE",
                            uid: that.authPhone.phone.replace(new RegExp(" ", "g"), "")
                        },
                        {
                            headers: {
                                "X-Client-Api-Version": serverApiVersion,
                                "X-Platform-Type": "WEB"
                            }
                        }
                    )
                    .then(response => {

                        if (window.dataLayer !== undefined) {
                            window.dataLayer.push({
                                'event': "event-to-ga",
                                'eventCategory': 'auth',
                                'eventAction': 'login',
                                'eventLabel': 'phone'
                            });
                        }
                        that.newUser = response.data;
                        setToken(response.data.access_token);

                        that.$store.commit("SET_TOKEN", response.data.access_token);

                        if(response.data.status === 'CONFIRM_PROFILE'){
                            that.currentUser.status = response.data.status;
                            that.$store.commit("SET_USER_INFO", that.currentUser);
                            that.showUserEditBlock = true;
                            this.email = this.currentUser.email
                        } else {
                            that.$store.commit("SET_TOKEN", response.data.access_token);

                            this.$store.dispatch("GetUserInfo").then(() => {
                                this.currentUser = this.$store.getters.getUserInfo;
                            });

                            // //update in analytic session
                            that.$bus.$emit("update-analytic", "login");
                        }
                        that.showUserBlock();
                    })
                    .catch(error => {
                        if (window.dataLayer !== undefined) {
                            window.dataLayer.push({
                                'event': "event-to-ga",
                                'eventCategory': 'auth',
                                'eventAction': 'error-login',
                                'eventLabel': 'phone'
                            });
                        }
                        that.authPhone.error = true;
                        that.isSmsCodeError = true;
                        // eslint-disable-next-line
                        console.log(error);
                    });
            },
            signInGoogle: function () {
                let that = this;
                //update in analytic session
                that.$bus.$emit("update-analytic");

                if (window.dataLayer !== undefined) {
                    window.dataLayer.push({
                        'event': "event-to-ga",
                        'eventCategory': 'auth',
                        'eventAction': 'try-login',
                        'eventLabel': 'google'
                    });
                }
                that.$gAuth
                    .signIn()
                    .then(auth => {
                        that.authCode = auth.getAuthResponse();
                        that.authGoogle();
                        // that.showUserBlock = true;
                        // that.showUserBlock();
                    })
                    // eslint-disable-next-line
                    .catch(error => {
                        if (window.dataLayer !== undefined) {
                            window.dataLayer.push({
                                'event': "event-to-ga",
                                'eventCategory': 'auth',
                                'eventAction': 'error-login',
                                'eventLabel': 'google-error'
                            });
                        }
                        // that.$notify({
                        //   title: "error",
                        //   type: "error",
                        //   message: "google error"
                        // });
                    });
            },
            onSignInSuccess(response) {

                let that = this;
                //update in analytic session
                that.$bus.$emit("update-analytic");
                let userLang = navigator.language || navigator.userLanguage;
                let timeZone = that.$moment.tz.guess();
                axios
                    .post(
                        "/api/uaa/login",
                        {
                            language: userLang,
                            time_zone: timeZone,
                            uid: response.authResponse.accessToken,
                            type: "FACEBOOK"
                        },
                        {
                            headers: {
                                "X-Client-Api-Version": serverApiVersion,
                                "X-Platform-Type": "WEB"
                            }
                        }
                    )
                    .then(response => {

                        //update in analytic session
                        that.$bus.$emit("update-analytic", 'login');

                        that.$emit("auth-confirmed", response.data);
                        // that.showUserBlock = true;
                        setToken(response.data.access_token);
                        that.$emit("change-auth", false);
                        if (window.dataLayer !== undefined) {
                            window.dataLayer.push({
                                'event': "event-to-ga",
                                'eventCategory': 'auth',
                                'eventAction': 'login',
                                'eventLabel': 'facebook'
                            });
                        }

                        that.newUser = response.data;
                        setToken(response.data.access_token);
                        that.$store.commit("SET_TOKEN", response.data.access_token);
                        this.$store.dispatch("GetUserInfo").then(() => {
                            this.currentUser = this.$store.getters.getUserInfo;
                        });

                        that.showUserBlock();
                    })
                    .catch(error => {
                        // eslint-disable-next-line
                        console.log(error);
                        if (window.dataLayer !== undefined) {
                            window.dataLayer.push({
                                'event': "event-to-ga",
                                'eventCategory': 'auth',
                                'eventAction': 'error-login',
                                'eventLabel': 'facebook'
                            });
                        }
                        that.$emit("change-auth", false);
                    });
            },
            // eslint-disable-next-line
            onSignInError(error) {

                //update in analytic session
                this.$bus.$emit("update-analytic");

                if (window.dataLayer !== undefined) {
                    window.dataLayer.push({
                        'event': "event-to-ga",
                        'eventCategory': 'auth',
                        'eventAction': 'error-login',
                        'eventLabel': 'facebook-error'
                    });
                }
            },
            // eslint-disable-next-line
            onInput: function ({number, isValid, country}) {
                this.authPhone.disableBtn = !isValid;
                this.authPhone.phone = number;
                this.authPhone.error = false;
            },
            showAuthPhone: function () {
                this.authPhone.show = true;
                //update in analytic session
                this.$bus.$emit("update-analytic");
            },
            authGoogle: function () {
                let that = this;
                let userLang = navigator.language || navigator.userLanguage;
                let timeZone = that.$moment.tz.guess();
                axios
                    .post(
                        "/api/uaa/login",
                        {
                            language: userLang,
                            time_zone: timeZone,
                            uid: that.authCode.id_token,
                            type: "GOOGLE"
                        },
                        {
                            headers: {
                                "X-Client-Api-Version": serverApiVersion,
                                "X-Platform-Type": "WEB"
                            }
                        }
                    )
                    .then(response => {
                        that.$emit("auth-confirmed", response.data);
                        setToken(response.data.access_token);
                        that.$emit("change-auth", false);
                        if (window.dataLayer !== undefined) {
                            window.dataLayer.push({
                                'event': "event-to-ga",
                                'eventCategory': 'auth',
                                'eventAction': 'login',
                                'eventLabel': 'google'
                            });
                        }

                        that.newUser = response.data;
                        that.$store.commit("SET_TOKEN", response.data.access_token);
                        this.$store.dispatch("GetUserInfo").then(() => {
                            this.currentUser = this.$store.getters.getUserInfo;
                        });

                        //update in analytic session
                        this.$bus.$emit("update-analytic", 'login');

                        that.showUserBlock();
                    })
                    .catch(error => {

                        // eslint-disable-next-line
                        console.log(error);
                        if (window.dataLayer !== undefined) {
                            window.dataLayer.push({
                                'event': "event-to-ga",
                                'eventCategory': 'auth',
                                'eventAction': 'error-login',
                                'eventLabel': 'google'
                            });
                        }
                        that.$emit("change-auth", false);
                    });
            },
            showUserBlock(){
                this.$emit('open-user-info-block');
            },
            setCurrentUser(authUser) {
                this.$store.commit("SET_USER_INFO", authUser);
            },
            disableContinueButton(){
                this.$emit('disable-continue-button')
            },
        }
    }
</script>
<style lang="scss">
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

<style lang="scss" scoped>

    .icon {
        width: 26px;
        height: 26px;
        margin-right: calc(var(--indent) / 2);
    }
</style>