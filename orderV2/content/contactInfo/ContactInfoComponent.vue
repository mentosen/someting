<template>
    <div class="tile__row tile__row--column-direction tile__row--padding-top">
        <div class="tile__row">
            <span :class="isMobile ? 'font--header-mobile': 'font--header'" class="font">{{$t('auth.contactInfo')}}</span>
<!--            <button style="width: auto" class="btn btn-v2-link btn-v2-link&#45;&#45;gray font font&#45;&#45;description" data-qa="button.contactInfo.back" @click="back">-->
<!--                <svg class="tile__row&#45;&#45;margin-right-half-x2" width="13" height="9" viewBox="0 0 13 9" fill="none" xmlns="http://www.w3.org/2000/svg">-->
<!--                    <path d="M2.44429 4L12.2372 4C12.5133 4 12.7372 4.22386 12.7372 4.5C12.7372 4.77614 12.5133 5 12.2372 5L2.44429 5L5.59074 8.14645C5.786 8.34171 5.786 8.65829 5.59074 8.85355C5.39547 9.04882 5.07889 9.04882 4.88363 8.85355L0.883629 4.85355C0.688367 4.65829 0.688367 4.34171 0.883629 4.14645L4.88363 0.146446C5.07889 -0.0488161 5.39547 -0.048816 5.59074 0.146446C5.786 0.341708 5.786 0.658292 5.59074 0.853553L2.44429 4Z" fill="#BCC0BC"/>-->
<!--                </svg>-->
<!--                {{$t("common.back")}}-->
<!--            </button>-->
        </div>

        <component
                :is="currentStateComponent"
                @open-user-info-block="openUserInfoBlock"
                @enable-continue-button="enableContinueButton"
                @disable-continue-button="disableContinueButton"
                @logout="logout"
                @close-loader="closeLoader"
        ></component>
    </div>
</template>

<script>
    import {ticketFlowState} from "../../../../api/ticket";
    import {removeToken} from '@/utils/auth';
    import hidePreloader from '../../../../utils/hidePreloader';
    import request from '@/utils/request';
    import {serverApiVersion} from '@/utils/constants';
    import {getToken} from "@/utils/auth";

    export default {
        name: "ContactInfoComponent",
        data(){
            return {
                currentStateComponent: ''
            }
        },
        mounted(){

            let that = this;
            this.$store.commit('SET_FLOW_STATE', ticketFlowState.PAYMENT);
            that.$emit('close-loader');
            that.$emit('reset-basket-info');

            that.token = this.$store.getters.getUserToken;
            if(that.token){
                this.currentStateComponent = () => import("./UserInfoComponent");
            } else {
                that.disableContinueButton();
                this.currentStateComponent = () => import("./AuthComponent");
            }
        },
        computed: {
            isMobile() {
                return this.$store.getters.isMobile;
            },
        },
        methods:{
            closeLoader(){
                this.$emit('close-loader');
            },
            back(){
                //update in analytic session
                this.$bus.$emit("update-analytic");
                this.$store.commit('SET_BACKCLICKED', true);
                this.$emit('back', '')
            },
            openUserInfoBlock(){
                this.currentStateComponent = () => import("./UserInfoComponent");
            },
            enableContinueButton(name){
                this.$emit('enable-continue-button');
            },
            disableContinueButton(){
                this.$emit('disable-continue-button');
            },
            logout() {
                let that = this;
                //update in analytic session
                that.$bus.$emit("update-analytic", 'logout');
                // removeToken();
                // that.$router.go()

                if (window.dataLayer !== undefined) {
                    window.dataLayer.push({
                        'event': "event-to-ga",
                        'eventCategory': 'auth',
                        'eventAction': 'logout',
                    });
                }
                that.$store.dispatch('FedLogOut').then(() => {
                    // this.$store.commit('SET_FLOW_STATE', ticketFlowState.USER_INFO);
                    this.$emit('disable-continue-button');
                    this.currentStateComponent = () => import("./AuthComponent");
                    // that.$router.go()
                }).catch(() => {
                    removeToken();
                    // that.$router.go()
                })
            },
        }
    }
</script>

<style lang="scss" scoped>
    @import "../../../../assets/styles/global/variables";

    .btn-v2-link {
        width: 50%;
        align-self: center;
        background: transparent;
        text-transform: none;
        color: #2979ff;

        &--gray {
            color: $gray450;
        }

        &:hover:not([disabled]) {
            color: darken(#2979ff, 15%)!important;
        }

    }
</style>