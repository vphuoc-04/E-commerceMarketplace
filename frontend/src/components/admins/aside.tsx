import { Link, useLocation } from "react-router-dom";
//import { useEffect } from "react";

import {
    Accordion,
    AccordionContent,
    AccordionItem,
    AccordionTrigger,
} from "@/components/ui/accordion";

// Constants
import { sidebarItem } from "../../constants/sidebar";

const Aside = () => {
    const location = useLocation();
    const segment = location.pathname.split('/')[2]
    console.log(segment);

    // const getOpenAccordionValue = () => {
    //     for (let groupIndex = 0; groupIndex < sidebarItem.length; groupIndex++) {
    //         const group = sidebarItem[groupIndex];
    //         for (let itemIndex = 0; itemIndex < group.items.length; itemIndex++) {
    //             const item = group.items[itemIndex];
    //             if (item.active.includes(segment)) {
    //                 return `item-${groupIndex}-${itemIndex}`
    //             }
    //         }
    //     }
    // }

    // const defaultValue = getOpenAccordionValue();

    return (
        <div className="w-70 h-screen bg-teal-600 text-white p-5 flex flex-col">
            <div className="text-lg font-bold mb-5">
                Logo
            </div>
            <div className="main-sidebar">
                {sidebarItem.map((group, index) => (
                    <div key={index}>
                        <span className="text-[13px]">{group.label}</span>
                        <div className="menu-category my-3">
                            <Accordion type="single" collapsible className="sidebar-accordion" defaultValue="">
                                {group.items.map((item, itemIndex) => (
                                    item.links.length === 0 ? (
                                        <Link 
                                            key={itemIndex} 
                                            to={'to' in item ? item.to : '#'}
                                            className={`flex items-center gap-2 px-3 py-5 w-full rounded-md cursor-pointer
                                                ${item.active.includes(segment) ? 'text-white bg-[rgba(255,255,255,0.1)]' : 'text-gray-300'}
                                            `}
                                        >
                                            {item.icons}
                                            <span>{item.label}</span>
                                        </Link>
                                    ) : (
                                        <AccordionItem key={itemIndex} value={`item-${index}-${itemIndex}`} className="border-b-0">
                                            <AccordionTrigger
                                                className={`
                                                    flex items-center gap-2 px-3 py-2 w-full cursor-pointer
                                                    [&>svg]:stroke-white
                                                    ${item.active.includes(segment) ? 'text-white bg-[rgba(255,255,255,0.1)]' : 'text-gray-300'}
                                                `}
                                            >
                                                <div className=" menu-label flex items-center gap-2 rounded-md w-50 py-3">
                                                    {item.icons}
                                                    <span className="no-underline">{item.label}</span>
                                                </div>
                                            </AccordionTrigger>

                                            <AccordionContent className="mt-3">
                                                <ul className="flex flex-col gap-2 ml-7 text-[15px]">
                                                    {item.links.map((link, linkIndex) => {
                                                        const isActiveLink = location.pathname === link.to;
                                                            return (
                                                                <li key={linkIndex}>
                                                                <Link to={link.to}>
                                                                    <p
                                                                    className={`
                                                                        py-3 pl-3 w-full rounded-[5px]
                                                                        ${isActiveLink
                                                                        ? 'text-white bg-[rgba(255,255,255,0.1)]'
                                                                        : 'text-gray-300 hover:bg-[rgba(255,255,255,0.1)]'}
                                                                    `}
                                                                    >
                                                                    {link.title}
                                                                    </p>
                                                                </Link>
                                                                </li>
                                                            );
                                                    })}
                                                </ul>
                                            </AccordionContent>
                                        </AccordionItem>      
                                    )             
                                ))}
                            </Accordion>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export { Aside };
