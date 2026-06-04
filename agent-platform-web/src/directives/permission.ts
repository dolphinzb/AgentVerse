import type { Directive, DirectiveBinding } from 'vue'
import { useUserStore } from '@/stores/user'

export const vPermission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const perm = binding.value as string
    if (!perm) return

    const userStore = useUserStore()
    if (!userStore.hasPermission(perm)) {
      el.parentNode?.removeChild(el)
    }
  },
}