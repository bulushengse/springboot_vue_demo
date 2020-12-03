<template>
  <div>
    <!-- <svg-icon class-name="setting-icon" icon-class="size" @click.stop="setting = true" /> -->
    <i class="el-icon-setting setting-icon" @click.stop="setting = true"/>
  </div>
</template>

<script>
export default {
  name: 'LayoutButton',
  computed: {
    setting: {
      get() {
        return this.$store.state.settings.showSettings
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'showSettings',
          value: val
        })
      }
    }
  }
}
</script>

<style scoped>
.setting-icon {
  cursor: pointer;
  font-size: 21px;
  margin-top: 14px;
}
</style>
