<script setup>
import { useElevatorWebSocket } from './composables/useElevatorWebSocket';
import ElevatorShaft from './components/ElevatorShaft.vue';
import HallPanel from './components/HallPanel.vue';
import CarPanel from './components/CarPanel.vue';
import TaskQueuePanel from './components/TaskQueuePanel.vue';

const { config, state, connected, sendHallCall, sendCarCall, sendDoorCommand } = useElevatorWebSocket();

</script>

<template>
  <div class="min-h-screen bg-gray-100 flex flex-col font-sans">
    <header class="bg-blue-600 text-white p-4 shadow-md flex justify-between items-center">
      <h1 class="text-2xl font-bold">Elevator Simulator</h1>
      <div>
        <span v-if="connected" class="px-3 py-1 bg-green-500 rounded-full text-sm font-semibold">Connected</span>
        <span v-else class="px-3 py-1 bg-red-500 rounded-full text-sm font-semibold">Disconnected</span>
      </div>
    </header>

    <main class="flex-grow p-6 flex gap-8 justify-center items-start overflow-auto">
      
      <!-- Elevator Shaft (Visualizer) -->
      <div class="bg-white p-6 rounded-xl shadow-lg border border-gray-200">
        <h2 class="text-xl font-bold mb-4 text-center">Building Shaft</h2>
        <ElevatorShaft :config="config" :state="state" />
      </div>

      <!-- Control Panels -->
      <div class="flex flex-col gap-6">
        
        <!-- Car Panel (Inside Elevator) -->
        <div class="bg-white p-6 rounded-xl shadow-lg border border-gray-200">
          <h2 class="text-xl font-bold mb-4 text-center">Car Panel</h2>
          <CarPanel 
            :config="config" 
            :state="state" 
            @car-call="sendCarCall" 
            @door-command="sendDoorCommand"
          />
        </div>

        <!-- Hall Panel (Outside on Floors) -->
        <div class="bg-white p-6 rounded-xl shadow-lg border border-gray-200 max-h-[600px] overflow-y-auto">
          <h2 class="text-xl font-bold mb-4 text-center">Hall Panels</h2>
          <div class="flex flex-col-reverse gap-4">
            <HallPanel 
              v-for="floor in config.totalFloors" 
              :key="floor" 
              :floor="floor" 
              :totalFloors="config.totalFloors"
              :state="state"
              @hall-call="(dir) => sendHallCall(floor, dir)" 
            />
          </div>
        </div>

      </div>

      <!-- Info Panels -->
      <div class="flex flex-col gap-6">
        <TaskQueuePanel :state="state" />
      </div>

    </main>
  </div>
</template>
