import { FaChartPie, FaUser, FaFingerprint  } from "react-icons/fa";

export const sidebarItem = [
    {
        label: 'MAIN',
        items: [
            {
                icons: <FaChartPie />,
                label: 'Dashboard',
                active: ['dashboard'],
                to: '/admin/dashboard',
                links: [
                    // { title: 'Overview', to: '/admin/dashboard' },
                    // { title: 'Details', to: '/admin/dashboard/details' }
                ] 
            }
        ]
    }, 
    {
        label: 'FEATURE',
        items: [
            {
                icons: <FaUser />,
                label: 'User',
                active: ['user'],
                links: [
                    { title: 'User management', to: '/admin/user/users' },
                    { title: 'User cataloge management', to: '/admin/user/catalogue' }
                ]
            },
            {
                icons: <FaFingerprint />,
                label: 'Permission',
                active: ['permission'],
                links: [
                    { title: 'Permission management', to: '/admin/permission/index' }
                ]
            }
        ]
    }
]