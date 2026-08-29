<script setup>
import { computed } from 'vue';

const props = defineProps({
  config: Object,
  state: Object
});

// Create array from 1 to totalFloors, reversed so top floor is at the top
const floors = computed(() => {
  const arr = [];
  for (let i = props.config.totalFloors; i >= 1; i--) {
    arr.push(i);
  }
  return arr;
});

const isDoorsOpen = computed(() => {
  return props.state.doorState === 'OPEN';
});
</script>

<template>
  <div class="relative w-48 border-4 border-gray-800 bg-gray-200">
    <div 
      v-for="floor in floors" 
      :key="floor" 
      class="h-16 border-b border-gray-400 relative flex items-center justify-end pr-2 text-gray-500 text-sm font-bold"
    >
      Floor {{ floor }}
      
      <!-- The Elevator Car -->
      <div 
        v-if="state.currentFloor === floor"
        class="absolute left-1 right-16 top-1 bottom-1 bg-gradient-to-br from-gray-300 to-gray-400 border-2 border-gray-600 rounded-sm shadow-inner flex items-center justify-center transition-all duration-1000 ease-in-out overflow-hidden"
      >
        <span class="text-xl font-bold text-gray-800 z-10">
          {{ state.currentDirection === 'UP' ? '↑' : state.currentDirection === 'DOWN' ? '↓' : '—' }}
        </span>

        <!-- Left Door -->
        <div 
          class="absolute top-0 bottom-0 left-0 bg-gray-500 border-r border-gray-700 transition-all duration-500"
          :class="isDoorsOpen ? 'w-1' : 'w-1/2'"
        ></div>
        <!-- Right Door -->
        <div 
          class="absolute top-0 bottom-0 right-0 bg-gray-500 border-l border-gray-700 transition-all duration-500"
          :class="isDoorsOpen ? 'w-1' : 'w-1/2'"
        ></div>

      </div>
    </div>
  </div>
</template>
