<template>
   <el-select v-model="size" placeholder="请选择" size="mini" style="width: 100px;" popper-class="select-option" @change="handleSetSize" >
    <el-option
      v-for="item of sizeOptions"
      :key="item.value"
      :label="item.label"
      :value="item.value"
      :disabled="size===item.value">
    </el-option>
  </el-select>
</template>

<script>
export default {
  data() {
    return {
      sizeOptions: [
        { label: 'Default', value: 'default' },
        { label: 'Medium', value: 'medium' },
        { label: 'Small', value: 'small' },
        { label: 'Mini', value: 'mini' }
      ],
      selectVal:''
    }
  },
  computed: {
    size:{
      get: function(){
        if(this.selectVal == ''){
          return this.$store.getters.size
        }else{
          return this.selectVal
        }
      },
      set: function(newSize){
        this.selectVal = newSize
      }
    }
  },
  methods: {
    handleSetSize() {
      this.$ELEMENT.size = this.selectVal
      this.$store.dispatch('app/setSize', this.selectVal)
      this.refreshView()
      this.$message({
        message: 'Switch Size Success',
        type: 'success'
      })
    },
    refreshView() {
      // In order to make the cached page re-rendered
      this.$store.dispatch('tagsView/delAllCachedViews', this.$route)

      const { fullPath } = this.$route

      this.$nextTick(() => {
        this.$router.replace({
          path: '/redirect' + fullPath
        })
      })
    }
  }

}
</script>

<style>
.select-option{
  z-index: 99999 !important;
}
</style>
