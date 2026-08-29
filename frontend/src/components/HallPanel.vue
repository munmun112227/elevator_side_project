<script setup>
import { computed } from 'vue';

const props = defineProps({
  floor: Number,
  totalFloors: Number,
  state: Object
});

const emit = defineEmits(['hall-call']);

const isUpRequested = computed(() => props.state.upHallRequests && props.state.upHallRequests.includes(props.floor));
const isDownRequested = computed(() => props.state.downHallRequests && props.state.downHallRequests.includes(props.floor));

const callUp = () => emit('hall-call', 'UP');
const callDown = () => emit('hall-call', 'DOWN');
</script>

<template>
  <div class="flex items-center gap-4 bg-gray-100 p-2 rounded border border-gray-300">
    <div class="w-16 font-bold text-gray-700 text-right">Floor {{ floor }}</div>
    <div class="flex flex-col gap-1 bg-gray-300 p-1 rounded">
      <button 
        v-if="floor < totalFloors"
        @click="callUp"
        class="w-8 h-8 flex items-center justify-center rounded transition-colors"
        :class="isUpRequested ? 'bg-yellow-400 text-black shadow-[0_0_8px_rgba(250,204,21,0.8)]' : 'bg-gray-200 hover:bg-gray-100 text-gray-600'"
      >
        ▲
      </button>
      <div v-else class="w-8 h-8"></div>
      
      <button 
        v-if="floor > 1"
        @click="callDown"
        class="w-8 h-8 flex items-center justify-center rounded transition-colors"
        :class="isDownRequested ? 'bg-yellow-400 text-black shadow-[0_0_8px_rgba(250,204,21,0.8)]' : 'bg-gray-200 hover:bg-gray-100 text-gray-600'"
      >
        ▼
      </button>
      <div v-else class="w-8 h-8"></div>
    </div>
  </div>
</template>
