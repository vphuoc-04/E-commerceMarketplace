import React from "react"

import { Link } from "react-router-dom"

import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbList,
  BreadcrumbSeparator,
} from "@/components/ui/breadcrumb"

interface PageHeadingProps {
    breadcrumb: {
        title: string,
        route: string
    }
}

const PageHeading: React.FC<PageHeadingProps> = ({ breadcrumb }) => {
    return (
        <>
            <div className="page-heading py-[20px] bg-white mb-3 shadow-sm">
                <div className="px-[15px]">
                    <h2 className="text-[24px] mb-[5px]">{breadcrumb.title}</h2>
                    <Breadcrumb>
                        <BreadcrumbList>
                            <BreadcrumbItem>
                                <Link to = "/admin/dashboard" >Dashboard</Link>
                            </BreadcrumbItem>
                            <BreadcrumbSeparator />
                            <BreadcrumbItem>
                                <Link to = {breadcrumb.route}>{breadcrumb.title}</Link>
                            </BreadcrumbItem>
                        </BreadcrumbList>
                    </Breadcrumb>
                </div>
            </div>
        </>
    )
}

export default PageHeading

