import { getOpenAPIDefinition } from '../api/generated/jserver';
import type { TaskDto } from '../api/generated/model';

const api = getOpenAPIDefinition();

export const taskService = {
  async getAllTasks(): Promise<TaskDto[]> {
    return await api.getAllTasks1();
  },

  async getTaskById(id: number): Promise<TaskDto> {
    return await api.getTaskById1(id);
  }
};
