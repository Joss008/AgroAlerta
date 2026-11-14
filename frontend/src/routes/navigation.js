import {
  CalendarRange,
  Droplets,
  LayoutDashboard,
  Map,
  ShoppingBag,
  Sprout,
  Users,
  Wallet,
  Wheat,
} from 'lucide-react';

export const NAV_ITEMS = [
  { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/agricultores', label: 'Agricultores', icon: Users },
  { to: '/parcelas', label: 'Parcelas', icon: Map },
  { to: '/cultivos', label: 'Cultivos', icon: Sprout },
  { to: '/campanas', label: 'Campañas', icon: CalendarRange },
  { to: '/riegos', label: 'Riegos', icon: Droplets },
  { to: '/gastos', label: 'Gastos', icon: Wallet },
  { to: '/cosechas', label: 'Cosechas', icon: Wheat },
  { to: '/ventas', label: 'Ventas', icon: ShoppingBag },
];
