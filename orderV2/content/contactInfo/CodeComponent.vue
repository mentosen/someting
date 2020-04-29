<template>
    <div style="max-width: 320px" class="tile__row tile__row--column-direction tile__row--padding-top">
        <span class="font font--auth">{{$t('auth.sendCode')}}</span>
        <span class="font font--bold font--bold-auth">{{phone}}</span>

        <button class="btn btn-v2--link btn-v2--link-underline tile__row--margin-top-half" @click="changeNumber">{{$t('auth.changeNumber')}}</button>

        <float-label
                class="tile__row--margin-top-origin"
                :dispatch="true"
                :read-only="false"
                :fixed="true"
                :label="$t('auth.code')"
                :is-error="!!errors.first('code')"
        >
            <input type="number"
                   name="code"
                   class="form__input"
                   ref="code"
                   autofocus="false"
                   v-model="code"
                   placeholder="XXXXXX"
                   data-qa="input.code"
            >
            </input>
            <span v-if="error" class="form__error">{{$t('error.wrongCode')}}</span>
        </float-label>

        <button style="width: 100%"
                class="btn-v2 btn-v2--colored-black
                tile__row--margin-top-origin"
                :disabled="!isAcceptEnable"
                data-qa="button.accept_code"
                @click="acceptCode">
            {{$t('auth.confirm')}}
        </button>

        <div v-if="!isResendSms" class="tile__row tile__row--margin-top-origin tile__row--flex-start">
            <span class="font font--subtitle font--subtitle-code tile__row--margin-right-half">{{$t('auth.resendCode')}}</span>
            <span class="font font--bold">{{date.format('mm:ss')}}</span>
        </div>
        <button v-if="isResendSms"
                class="btn btn-v2--link
                btn-v2--link-underline
                tile__row--margin-top-origin" data-qa="button.resend_sms" @click="resendSms">
            {{$t('auth.sendNewCode')}}
        </button>

    </div>
</template>

<script>
    import moment from 'moment';

    export default {
        name: "CodeComponent",
        props:{
            error:{
                type:Boolean,
                required: true
            }
        },
        data(){
            return {
                code: '',
                phone: '',
                date: moment(60 * 1000),
                isResendSms: false,
                isAcceptEnable: false
            }
        },
        mounted(){
            this.setResendTimer();
            this.phone = this.$store.getters.PHONE;
        },
        watch:{
            code(){
                if(this.code.length === 6 && !this.isAcceptEnable){
                    this.isAcceptEnable = true
                }
            }
        },
        methods:{
            setResendTimer(){
                this.date = moment(60 * 1000);
                let timer = setInterval(() => {
                    if(this.date > moment(0)) {
                        this.date = moment(this.date.subtract(1, 'seconds'))
                    } else {
                        clearInterval(timer);

                        this.isResendSms = true;
                    }
                }, 1000);
            },
            resendSms(){
                this.setResendTimer();
                this.isResendSms = false;
                this.$emit('resend-sms');
            },
            changeNumber(){
                this.$emit('change-number');
            },
            acceptCode(){
                this.$emit('accept-code', this.code);
            }
        }
    }
</script>

<style scoped>

</style>