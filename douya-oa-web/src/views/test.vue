<template>
  <div>
    <div>账号切换</div>
    <!-- <button @click="wjl()" type="default" size="mini">王经理</button> -->
    <button @click="zhang3()" type="default" size="mini">张三经理</button>
    <button @click="li4()" type="default" size="mini">李四总经理</button>
    <button @click="admin()" type="default" size="mini">admin</button>
    <div>当前token：{{ token }}</div>
  </div>
</template>

<script>
export default {
  name: "Test",

  data() {
    return {
      token: ''
    };
  },

  created(){
    this.token = window.localStorage.getItem('token')
  },

  methods: {
    wjl() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNDM3NjMwNDIx1FEqLU4t8kxRsjKCMPMSc1OBWsqzcpRqAb4pVZBAAAAA.-bW4hJ5UptFiCRsL6jVUa4XbgVwdsfVGfP8gJ-tOVbp7D3JOb8sylbBDLUhTZt6FJsYhlA5uYz8HSRck_pbaCA'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    zhang3() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNDc1sjA2MDM20VEqLU4t8kxRsjKHMPMSc1OBWqoyEvPSjZVqAUBdLMNDAAAA.N8ikuLD8VUF8DZHp4pa_gLpyntd5dDZxGMZzPcQO5kBjH7i__vGPzJtzQA3__BkZpkITq-tUp3GYgbj62QTOhQ'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    li4() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNDc1Mje1MDI201EqLU4t8kxRsjKBMPMSc1OBWnIyTZRqAdN1XRZAAAAA.VFkX_rjPszzUU-Hv011kMkH212L9etXOtGdmdvZPva36v0QEAaMJJYmIuJQblw-XSxfjdw6x5hSZUUqm4p0YEw'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    admin() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNDc1Mje1MDQw1FEqLU4t8kwBikGYeYm5qUAtiSm5mXlKtQAilr37QgAAAA.iLB7C4dDyoVkcBA4QX5oX60zdEdItfMFVcmRVM15kwVctgDB1akOSxLEPMbuqlACdble-IO0oedcy-zTY_kHAw'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    }
  }
}
</script>
