import { useMemo } from 'react';
import {
  CalendarRange,
  Compass,
  Droplets,
  Layers,
  Map as MapIcon,
  Maximize2,
  RefreshCw,
  ShoppingBag,
  Sprout,
  TrendingUp,
  Users,
  Wallet,
  Wheat,
} from 'lucide-react';
import PageHeader from '../../components/common/PageHeader';
import StatCard from '../../components/common/StatCard';
import ErrorAlert from '../../components/common/ErrorAlert';
import Loading from '../../components/common/Loading';
import Badge from '../../components/common/Badge';
import DataTable from '../../components/tables/DataTable';
import AreaChartCard from '../../components/charts/AreaChartCard';
import BarChartCard from '../../components/charts/BarChartCard';
import PieChartCard from '../../components/charts/PieChartCard';
import { CHART_COLORS } from '../../components/charts/chartTheme';
import { BentoCard, BentoGrid } from '../../components/motion/BentoGrid';
import AnimatedContent from '../../components/motion/AnimatedContent';
import cosechas from '../../services/cosechas';
import dashboard from '../../services/dashboard';
import useFetch from '../../hooks/useFetch';
import { getApiMessage } from '../../utils/errors';
import {
  formatDate,
  formatHectares,
  formatInteger,
  formatKg,
  formatM3,
  formatNumber,
  formatRendimiento,
  formatSoles,
} from '../../utils/format';

function buildComparativo(ventas, gastosPorCampana) {
  const map = new Map();

  for (const venta of ventas) {
    map.set(venta.idCampana, {
      idCampana: venta.idCampana,
      nombreCampana: venta.nombreCampana || `#${venta.idCampana}`,
      ventas: Number(venta.montoSoles) || 0,
      gastos: 0,
    });
  }

  for (const gasto of gastosPorCampana) {
    const entry = map.get(gasto.idCampana) || {
      idCampana: gasto.idCampana,
      nombreCampana: gasto.nombreCampana || `#${gasto.idCampana}`,
      ventas: 0,
      gastos: 0,
    };
    entry.gastos = Number(gasto.montoSoles) || 0;
    map.set(gasto.idCampana, entry);
  }

  return Array.from(map.values());
}

function rentabilidadBadge(value) {
  const number = Number(value);
  if (!Number.isFinite(number) || number === 0) {
    return <Badge variant="default">{formatSoles(value)}</Badge>;
  }
  return (
    <Badge variant={number > 0 ? 'success' : 'danger'}>{formatSoles(value)}</Badge>
  );
}

const rentabilidadColumns = [
  { key: 'nombreCampana', header: 'Campaña' },
  {
    key: 'produccionKg',
    header: 'Producción',
    align: 'right',
    render: (row) => formatKg(row.produccionKg),
  },
  {
    key: 'ventasSoles',
    header: 'Ventas',
    align: 'right',
    render: (row) => formatSoles(row.ventasSoles),
  },
  {
    key: 'gastosSoles',
    header: 'Gastos',
    align: 'right',
    render: (row) => formatSoles(row.gastosSoles),
  },
  {
    key: 'rentabilidadSoles',
    header: 'Rentabilidad',
    align: 'right',
    render: (row) => rentabilidadBadge(row.rentabilidadSoles),
  },
  {
    key: 'rendimientoKgHa',
    header: 'Rendimiento',
    align: 'right',
    render: (row) => formatRendimiento(row.rendimientoKgHa),
  },
  {
    key: 'consumoAguaM3',
    header: 'Agua',
    align: 'right',
    render: (row) => formatM3(row.consumoAguaM3),
  },
];

function buildHistoricoProduccion(rows) {
  const byDate = new Map();
  for (const row of rows) {
    if (!row?.fechaCosecha) continue;
    const key = String(row.fechaCosecha).slice(0, 10);
    const kg = Number(row.cantidadKg) || 0;
    byDate.set(key, (byDate.get(key) || 0) + kg);
  }
  return Array.from(byDate.entries())
    .sort(([a], [b]) => a.localeCompare(b))
    .map(([fecha, cantidadKg]) => ({ fecha, cantidadKg }));
}

const AREA_TOTAL_PROMEDIO_HA = 20000;

export default function DashboardPage() {
  const resumen = useFetch(dashboard.resumen);
  const produccion = useFetch(dashboard.produccion);
  const ventas = useFetch(dashboard.ventas);
  const gastos = useFetch(dashboard.gastos);
  const rentabilidad = useFetch(dashboard.rentabilidad);
  const cosechasHist = useFetch(() => cosechas.list({ page: 0, size: 100 }));

  const refreshAll = () => {
    resumen.reload();
    produccion.reload();
    ventas.reload();
    gastos.reload();
    rentabilidad.reload();
    cosechasHist.reload();
  };

  const stats = useMemo(() => {
    const data = resumen.data || {};
    const areaUtilizada =
      data.areaTotalHectareas == null ? null : Number(data.areaTotalHectareas);
    const areaDisponible =
      areaUtilizada == null || Number.isNaN(areaUtilizada)
        ? null
        : Math.max(0, AREA_TOTAL_PROMEDIO_HA - areaUtilizada);

    const rentabilidadSoles = data.rentabilidadTotalSoles;
    const rentabilidadTone =
      rentabilidadSoles == null
        ? 'default'
        : Number(rentabilidadSoles) > 0
          ? 'positive'
          : Number(rentabilidadSoles) < 0
            ? 'negative'
            : 'default';

    return [
      { label: 'Agricultores', value: formatInteger(data.agricultores), icon: Users },
      { label: 'Parcelas', value: formatInteger(data.parcelas), icon: MapIcon },
      { label: 'Cultivos', value: formatInteger(data.cultivos), icon: Sprout },
      {
        label: 'Campañas',
        value: formatInteger(data.campanas),
        icon: CalendarRange,
        hint: `${formatInteger(data.campanasActivas)} activas`,
      },
      {
        label: 'Área total promedio',
        value: formatHectares(AREA_TOTAL_PROMEDIO_HA),
        icon: Maximize2,
        hint: 'Referencia fija',
      },
      {
        label: 'Área utilizada',
        value: formatHectares(areaUtilizada),
        icon: Layers,
      },
      {
        label: 'Hectáreas disponibles',
        value: formatHectares(areaDisponible),
        icon: Compass,
        tone: areaDisponible != null && areaDisponible > 0 ? 'positive' : 'default',
      },
      {
        label: 'Producción total',
        value: formatKg(data.produccionTotalKg),
        icon: Wheat,
      },
      { label: 'Ventas', value: formatSoles(data.ventasTotalSoles), icon: ShoppingBag },
      { label: 'Gastos', value: formatSoles(data.gastosTotalSoles), icon: Wallet },
      {
        label: 'Rentabilidad',
        value: formatSoles(rentabilidadSoles),
        icon: TrendingUp,
        tone: rentabilidadTone,
      },
      { label: 'Agua consumida', value: formatM3(data.aguaTotalM3), icon: Droplets },
    ];
  }, [resumen.data]);

  const comparativo = useMemo(() => {
    const ventasRows = Array.isArray(ventas.data) ? ventas.data : [];
    const gastosRows = Array.isArray(gastos.data?.porCampana)
      ? gastos.data.porCampana
      : [];
    return buildComparativo(ventasRows, gastosRows);
  }, [ventas.data, gastos.data]);

  const produccionRows = Array.isArray(produccion.data) ? produccion.data : [];
  const gastosPorConcepto = Array.isArray(gastos.data?.porConcepto)
    ? gastos.data.porConcepto
    : [];
  const rentabilidadRows = Array.isArray(rentabilidad.data) ? rentabilidad.data : [];
  const historicoProduccion = useMemo(() => {
    const rows = Array.isArray(cosechasHist.data?.content)
      ? cosechasHist.data.content
      : [];
    return buildHistoricoProduccion(rows);
  }, [cosechasHist.data]);

  return (
    <div className="space-y-4">
      <PageHeader
        title="Dashboard"
        description="Indicadores generales de la actividad agrícola de Cañete: producción, ventas, gastos y rentabilidad por campaña."
        actions={
          <button
            type="button"
            onClick={refreshAll}
            className="inline-flex items-center justify-center gap-1.5 rounded-lg border border-stone-300 bg-white px-3.5 py-2 text-sm font-medium text-stone-700 transition hover:bg-stone-50"
          >
            <RefreshCw size={15} aria-hidden="true" />
            Actualizar
          </button>
        }
      />

      <section aria-label="Indicadores generales">
        {resumen.loading ? (
          <div className="rounded-xl border border-stone-200 bg-white">
            <Loading label="Cargando indicadores…" />
          </div>
        ) : resumen.error ? (
          <ErrorAlert
            title="No se pudieron cargar los indicadores"
            message={getApiMessage(resumen.error)}
            onRetry={resumen.reload}
          />
        ) : (
          <BentoGrid
            className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4"
            glowColor="5, 150, 105"
            spotlightRadius={260}
            enableBorderGlow
            enableSpotlight
            clickEffect
            intro
          >
            {stats.map((stat) => (
              <BentoCard
                key={stat.label}
                glowColor={stat.tone === 'negative' ? '225, 29, 72' : undefined}
              >
                <StatCard
                  label={stat.label}
                  value={stat.value}
                  hint={stat.hint}
                  icon={stat.icon}
                  tone={stat.tone}
                  className="h-full"
                />
              </BentoCard>
            ))}
          </BentoGrid>
        )}
      </section>

      <AnimatedContent
        className="grid grid-cols-1 gap-4 xl:grid-cols-3"
        distance={24}
        blur={4}
        duration={0.5}
        stagger={0.08}
        threshold={0.08}
      >
        <BarChartCard
          className="xl:col-span-2"
          title="Producción por campaña"
          description="Kilogramos cosechados en cada campaña."
          data={produccionRows}
          xKey="nombreCampana"
          series={[{ dataKey: 'produccionKg', name: 'Producción (kg)', fill: CHART_COLORS.emerald }]}
          legend={[{ name: 'Producción (kg)', color: CHART_COLORS.emerald }]}
          valueFormatter={(value) => formatNumber(value)}
          loading={produccion.loading}
          error={produccion.error}
          onRetry={produccion.reload}
          emptyTitle="Sin producción registrada"
          emptyDescription="Registre cosechas para ver la producción por campaña."
        />
        <PieChartCard
          title="Gastos por concepto"
          description="Distribución del gasto total."
          data={gastosPorConcepto}
          nameKey="concepto"
          valueKey="montoSoles"
          valueFormatter={(value) => formatSoles(value)}
          loading={gastos.loading}
          error={gastos.error}
          onRetry={gastos.reload}
          emptyTitle="Sin gastos registrados"
          emptyDescription="Cuando registre gastos, aquí verá en qué se invierte."
        />
      </AnimatedContent>

      <AnimatedContent distance={24} blur={4} duration={0.5} threshold={0.08}>
        <BarChartCard
          title="Ventas vs. gastos por campaña"
          description="Comparativo en soles de lo ingresado y lo invertido."
          data={comparativo}
          xKey="nombreCampana"
          series={[
            { dataKey: 'ventas', name: 'Ventas (S/)', fill: CHART_COLORS.emerald },
            { dataKey: 'gastos', name: 'Gastos (S/)', fill: CHART_COLORS.amber },
          ]}
          legend={[
            { name: 'Ventas (S/)', color: CHART_COLORS.emerald },
            { name: 'Gastos (S/)', color: CHART_COLORS.amber },
          ]}
          valueFormatter={(value) => formatNumber(value)}
          height={280}
          loading={ventas.loading || gastos.loading}
          error={ventas.error || gastos.error}
          onRetry={() => {
            ventas.reload();
            gastos.reload();
          }}
          emptyTitle="Sin datos comparables"
          emptyDescription="Registre ventas y gastos para compararlos por campaña."
        />
      </AnimatedContent>

      <AnimatedContent
        className="grid grid-cols-1 gap-4 xl:grid-cols-2"
        distance={24}
        blur={4}
        duration={0.5}
        stagger={0.08}
        threshold={0.08}
      >
        <BarChartCard
          title="Rendimiento estadístico por campaña"
          description="Productividad promedio expresada en kilogramos por hectárea."
          data={produccionRows}
          xKey="nombreCampana"
          series={[
            {
              dataKey: 'rendimientoKgHa',
              name: 'Rendimiento (kg/ha)',
              fill: CHART_COLORS.teal,
            },
          ]}
          legend={[{ name: 'Rendimiento (kg/ha)', color: CHART_COLORS.teal }]}
          valueFormatter={(value) => formatNumber(value, { max: 1 })}
          height={280}
          loading={produccion.loading}
          error={produccion.error}
          onRetry={produccion.reload}
          emptyTitle="Sin rendimiento registrado"
          emptyDescription="Registre cosechas en sus parcelas para calcular el rendimiento por hectárea."
        />
        <AreaChartCard
          title="Histórico de producción en el tiempo"
          description="Evolución cronológica de los kilogramos cosechados según fecha de cosecha."
          data={historicoProduccion}
          xKey="fecha"
          dataKey="cantidadKg"
          seriesName="Cosecha (kg)"
          color={CHART_COLORS.sky}
          legend={[{ name: 'Cosecha (kg)', color: CHART_COLORS.sky }]}
          valueFormatter={(value) => formatNumber(value)}
          xTickFormatter={formatDate}
          height={280}
          loading={cosechasHist.loading}
          error={cosechasHist.error}
          onRetry={cosechasHist.reload}
          emptyTitle="Sin historial de cosechas"
          emptyDescription="Registre cosechas con fecha para visualizar la tendencia histórica."
        />
      </AnimatedContent>

      <AnimatedContent distance={24} blur={4} duration={0.5} threshold={0.08}>
        <DataTable
          columns={rentabilidadColumns}
          rows={rentabilidadRows}
          loading={rentabilidad.loading}
          error={rentabilidad.error}
          onRetry={rentabilidad.reload}
          emptyTitle="Sin rentabilidad por campaña"
          emptyDescription="Cuando registre cosechas, ventas y gastos, aquí verá la rentabilidad de cada campaña."
        />
      </AnimatedContent>
    </div>
  );
}
