import { lazy, Suspense } from 'react';
import { Link, Route, Routes, Navigate } from 'react-router-dom';
import AppLayout from '../components/layout/AppLayout';
import PageHeader from '../components/common/PageHeader';
import Loading from '../components/common/Loading';

const DashboardPage = lazy(() => import('../modules/dashboard/DashboardPage'));
const AgricultoresPage = lazy(() => import('../modules/agricultores/AgricultoresPage'));
const ParcelasPage = lazy(() => import('../modules/parcelas/ParcelasPage'));
const CultivosPage = lazy(() => import('../modules/cultivos/CultivosPage'));
const CampanasPage = lazy(() => import('../modules/campanas/CampanasPage'));
const RiegosPage = lazy(() => import('../modules/riegos/RiegosPage'));
const GastosPage = lazy(() => import('../modules/gastos/GastosPage'));
const CosechasPage = lazy(() => import('../modules/cosechas/CosechasPage'));
const VentasPage = lazy(() => import('../modules/ventas/VentasPage'));

function NotFoundPage() {
  return (
    <div>
      <PageHeader title="Página no encontrada" />
      <div className="rounded-xl border border-stone-200 bg-white px-6 py-16 text-center">
        <p className="text-sm text-stone-500">
          La ruta que solicitó no existe o fue movida.
        </p>
        <Link
          to="/dashboard"
          className="mt-4 inline-flex rounded-lg bg-emerald-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-emerald-700"
        >
          Volver al dashboard
        </Link>
      </div>
    </div>
  );
}

export default function AppRoutes() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route
          path="dashboard"
          element={
            <Suspense fallback={<Loading label="Cargando dashboard…" />}>
              <DashboardPage />
            </Suspense>
          }
        />
        <Route
          path="agricultores"
          element={
            <Suspense fallback={<Loading label="Cargando agricultores…" />}>
              <AgricultoresPage />
            </Suspense>
          }
        />
        <Route
          path="parcelas"
          element={
            <Suspense fallback={<Loading label="Cargando parcelas…" />}>
              <ParcelasPage />
            </Suspense>
          }
        />
        <Route
          path="cultivos"
          element={
            <Suspense fallback={<Loading label="Cargando cultivos…" />}>
              <CultivosPage />
            </Suspense>
          }
        />
        <Route
          path="campanas"
          element={
            <Suspense fallback={<Loading label="Cargando campañas…" />}>
              <CampanasPage />
            </Suspense>
          }
        />
        <Route
          path="riegos"
          element={
            <Suspense fallback={<Loading label="Cargando riegos…" />}>
              <RiegosPage />
            </Suspense>
          }
        />
        <Route
          path="gastos"
          element={
            <Suspense fallback={<Loading label="Cargando gastos…" />}>
              <GastosPage />
            </Suspense>
          }
        />
        <Route
          path="cosechas"
          element={
            <Suspense fallback={<Loading label="Cargando cosechas…" />}>
              <CosechasPage />
            </Suspense>
          }
        />
        <Route
          path="ventas"
          element={
            <Suspense fallback={<Loading label="Cargando ventas…" />}>
              <VentasPage />
            </Suspense>
          }
        />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  );
}
