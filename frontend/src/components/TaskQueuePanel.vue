<script setup>
import { computed } from 'vue';

const props = defineProps({
  state: Object
});
</script>

<template>
  <div class="bg-gray-800 p-6 rounded-xl shadow-lg border border-gray-600 text-white w-64">
    <h2 class="text-xl font-bold mb-4 text-center border-b border-gray-600 pb-2">任務清單</h2>
    
    <div class="mb-4">
      <h3 class="text-green-400 font-semibold mb-2 flex items-center gap-2">
        <span>▲</span> 上樓需求 (Up Queue)
      </h3>
      <div class="flex flex-wrap gap-2">
        <span 
          v-for="floor in state.upRequests" 
          :key="'up-' + floor"
          class="bg-green-600 text-white px-2 py-1 rounded shadow"
        >
          {{ floor }} 樓
        </span>
        <span v-if="!state.upRequests || state.upRequests.length === 0" class="text-gray-400 text-sm">無任務</span>
      </div>
    </div>

    <div>
      <h3 class="text-red-400 font-semibold mb-2 flex items-center gap-2">
        <span>▼</span> 下樓需求 (Down Queue)
      </h3>
      <div class="flex flex-wrap gap-2">
        <span 
          v-for="floor in state.downRequests" 
          :key="'down-' + floor"
          class="bg-red-600 text-white px-2 py-1 rounded shadow"
        >
          {{ floor }} 樓
        </span>
        <span v-if="!state.downRequests || state.downRequests.length === 0" class="text-gray-400 text-sm">無任務</span>
      </div>
    </div>
  </div>
</template>
