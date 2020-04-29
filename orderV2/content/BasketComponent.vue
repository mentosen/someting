<template>
    <div class="tile__row tile__row--column-direction tile__row--paddings">
        <div style="width: 100%">
            <div style="width: 100%" class="tile__row
            tile__row--column-direction
            tile__row--margin-bottom
            tile__row--paddings-left-right-half
            tile__row--align-start
            tile__row--padding-top
            tile__row--padding-bottom block" v-for="(ticket, index) in tickets">
                <span class="font font--survey-label">{{$t('tickets.ticketNumber', [index+1])}} </span>
                <div class="tile__row">
                    <span class="font font--bold font--bold-title">{{ticket.name}}</span>
                    <span class="font font--title">{{$t('currencies.uah', [ticket.type !== 'DONATE' ? ticket.discountAmount > 0 ? ticket.discountPrice / 100 : ticket.price / 100 : ticket.donateAmountFull])}}</span>
                </div>
            </div>
        </div>

        <div class="tile__row tile__row--margin-top-origin">
            <button class="btn-v2 btn-v2--link btn-v2--link-basket btn-v2--link-basket-left pop-up-footer" data-qa="button.basket.change_count" @click="changeTicket">{{$t('tickets.changeCount')}}</button>
            <button class="btn-v2 btn-v2--link btn-v2--link-basket btn-v2--link-basket-right pop-up-footer font--subtitle font--subtitle-color-gray" data-qa="button.close_basket" @click="close">
                {{$t('common.hide')}}
                <svg width="24" height="24" viewBox="0 0 15 15" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M10.2778 6.343C10.4612 6.18576 10.7374 6.207 10.8947 6.39046C11.0519 6.57391 11.0307 6.85011 10.8472 7.00735L7.78472 9.63235C7.62088 9.77278 7.37912 9.77278 7.21528 9.63235L4.15279 7.00735C3.96933 6.85011 3.94809 6.57391 4.10533 6.39046C4.26258 6.207 4.53877 6.18576 4.72223 6.343L7.5 8.72395L10.2778 6.343Z" fill="#636E69"/>
                </svg>
            </button>
        </div>
    </div>
</template>

<script>
    export default {
        name: "BasketComponent",
        props:{
            tickets: {
                type: Array,
                required: true
            }
        },
        data(){
            return {
                ticketList: []
            }
        },
        methods: {
            changeTicket(){
                this.$store.commit('SET_BACKCLICKED', true);
                this.$emit('change-tickets', '')
            },
            close(){
                this.$store.commit('SET_BACKCLICKED', true);
                this.$emit('close')
            }
        }
    }
</script>

<style scoped>

    .block {
        background: #EBEEEB;
        border-radius: 4px;
    }

    .pop-up-footer {
        z-index: 2;
        position: sticky;
        bottom: 0;
        display: flex;
    }
</style>