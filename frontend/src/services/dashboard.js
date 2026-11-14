import api from './api';

const dashboard = {
  resumen: () => api.get('/dashboard/resumen'),
  produccion: () => api.get('/dashboard/produccion'),
  ventas: () => api.get('/dashboard/ventas'),
  gastos: () => api.get('/dashboard/gastos'),
  rentabilidad: () => api.get('/dashboard/rentabilidad'),
};

export default dashboard;
