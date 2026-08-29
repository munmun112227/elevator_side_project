<script setup>
import { computed } from 'vue';

const props = defineProps({
  config: Object,
  state: Object
});

const emit = defineEmits(['car-call', 'door-command']);

const isFloorRequested = (floor) => {
  return props.state.carRequests && props.state.carRequests.includes(floor);
};

const pressButton = (floor) => {
  emit('car-call', floor);
};

const commandDoor = (action) => {
  emit('door-command', action);
};
</script>

<template>
  <div class="bg-gray-800 p-4 rounded-lg text-white w-48">
    <div class="bg-black text-red-500 font-mono text-2xl text-center p-2 mb-4 rounded border-2 border-gray-600 flex justify-between items-center px-4">
      <span>{{ state.currentDirection === 'UP' ? '▲' : state.currentDirection === 'DOWN' ? '▼' : '■' }}</span>
      <span>{{ state.currentFloor }}</span>
    </div>
    
    <div class="grid grid-cols-2 gap-3 mb-4">
      <button 
        v-for="floor in config.totalFloors" 
        :key="floor"
        @click="pressButton(floor)"
        class="w-12 h-12 rounded-full font-bold transition-colors mx-auto"
        :class="[
          isFloorRequested(floor) 
            ? 'bg-yellow-400 text-black border-2 border-yellow-200 shadow-[0_0_10px_rgba(250,204,21,0.8)]' 
            : 'bg-gray-600 hover:bg-gray-500 text-white border-2 border-gray-500'
        ]"
      >
        {{ floor }}
      </button>
    </div>

    <div class="flex justify-between mt-4 border-t border-gray-600 pt-4">
      <button 
        @click="commandDoor('OPEN')"
        class="w-16 h-10 bg-gray-600 hover:bg-gray-500 text-white font-bold rounded shadow-md border border-gray-500 flex items-center justify-center text-xl"
        title="開門"
      >
        <span class="text-sm px-1">◀</span>|
      </button>
      <button 
        @click="commandDoor('CLOSE')"
        class="w-16 h-10 bg-gray-600 hover:bg-gray-500 text-white font-bold rounded shadow-md border border-gray-500 flex items-center justify-center text-xl"
        title="關門"
      >
        |<span class="text-sm px-1">▶</span>
      </button>
    </div>
  </div>
</template>
