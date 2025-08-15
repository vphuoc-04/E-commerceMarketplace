import { useState, useEffect } from "react";
import {
    Card, 
    CardContent, 
    CardHeader, 
    CardTitle, 
    CardDescription, 
    CardFooter,
} from "@/components/ui/card";

import {
    Table, 
    TableBody, 
    TableCell, 
    TableHead, 
    TableHeader, 
    TableRow,
} from "@/components/ui/table";

import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";

import { Checkbox } from "@/components/ui/checkbox";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { UserCatalogue } from "@/types/UserCatalogue";

import { 
    fetchUserCatalogue, 
    createUserCatalogue, 
    updateUserCatalogue, 
    deleteUserCatalogue 
} from "@/services/UserCatalogueService";
import { pagination } from "@/services/UserService";
import { getAllPermissions, Permission } from "@/services/PermissionService";
import PageHeading from "@/components/admins/heading";

const Catalogue = () => {
    const [userCatalogue, setUserCatalogue] = useState<UserCatalogue[]>([]);
    const [name, setName] = useState("");
    const [publish, setPublish] = useState("1");
    const [editingId, setEditingId] = useState<number | null>(null);
    const [allUsers, setAllUsers] = useState<any[]>([]);
    const [selectedUsers, setSelectedUsers] = useState<number[]>([]);
    const [allPermissions, setAllPermissions] = useState<Permission[]>([]);
    const [selectedPermissions, setSelectedPermissions] = useState<number[]>([]);

    useEffect(() => {
        const fetchData = async () => {
            const data = await fetchUserCatalogue();
            setUserCatalogue(data);

            const userData = await pagination(1);
            setAllUsers(userData?.users || []);

            const permissionData = await getAllPermissions();
            setAllPermissions(permissionData || []);
        };
        fetchData();
    }, []);

    const handleCreateOrUpdate = async () => {
        if (!name.trim()) return;

        if (editingId !== null) {
            await updateUserCatalogue(editingId, name, publish);
            setEditingId(null);
        } else {
            await createUserCatalogue(name, publish, selectedUsers, selectedPermissions);
        }
        setName("");
        setSelectedUsers([]);
        setSelectedPermissions([]);
        const updatedData = await fetchUserCatalogue();
        setUserCatalogue(updatedData);
    };

    const handleEdit = (catalogue: UserCatalogue) => {
        setEditingId(catalogue.id);
        setName(catalogue.name);
        setPublish(String(catalogue.publish));
    };

    const handleDelete = async (id: number) => {
        const success = await deleteUserCatalogue(id);
        if (success) {
            setUserCatalogue(userCatalogue.filter(item => item.id !== id));
        }
    };

    const breadcrumb = {
        title: "Quản lý nhóm người dùng",
        route: "/admin/user/catalogue"
    }

    return (
        <>
            <PageHeading breadcrumb=  { breadcrumb } />
            <div className="container p-3">
                <Card>
                    <CardHeader>
                        <CardTitle>User Catalogue</CardTitle>
                        <CardDescription>Manage user catalogues</CardDescription>
                    </CardHeader>
                    <CardContent>
                        <div className="flex gap-2 mb-4">
                            <Input
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                placeholder="User catalogue name..."
                            />
                            <Select value={publish} onValueChange={setPublish}>
                                <SelectTrigger className="w-[180px]">
                                    <SelectValue placeholder="Status" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="0">Inactive</SelectItem>
                                    <SelectItem value="1">Active</SelectItem>
                                    <SelectItem value="2">Archived</SelectItem>
                                </SelectContent>
                            </Select>
                            {/* Chọn nhóm thành viên bằng checkbox list */}
                            <div className="flex flex-col max-h-32 overflow-y-auto border rounded p-2 w-[220px]">
                                <span className="text-xs text-gray-500 mb-1">Chọn thành viên</span>
                                {allUsers.map((user) => (
                                    <label key={user.id} className="flex items-center gap-2 cursor-pointer">
                                        <input
                                            type="checkbox"
                                            checked={selectedUsers.includes(user.id)}
                                            onChange={e => {
                                                if (e.target.checked) {
                                                    setSelectedUsers(prev => [...prev, user.id]);
                                                } else {
                                                    setSelectedUsers(prev => prev.filter(id => id !== user.id));
                                                }
                                            }}
                                        />
                                        <span>{user.firstName} {user.lastName} ({user.email})</span>
                                    </label>
                                ))}
                            </div>
                            {/* Chọn nhóm quyền bằng checkbox list */}
                            <div className="flex flex-col max-h-32 overflow-y-auto border rounded p-2 w-[220px]">
                                <span className="text-xs text-gray-500 mb-1">Chọn nhóm quyền</span>
                                {allPermissions.map((permission) => (
                                    <label key={permission.id} className="flex items-center gap-2 cursor-pointer">
                                        <input
                                            type="checkbox"
                                            checked={selectedPermissions.includes(permission.id)}
                                            onChange={e => {
                                                if (e.target.checked) {
                                                    setSelectedPermissions(prev => [...prev, permission.id]);
                                                } else {
                                                    setSelectedPermissions(prev => prev.filter(id => id !== permission.id));
                                                }
                                            }}
                                        />
                                        <span>{permission.name}</span>
                                    </label>
                                ))}
                            </div>
                            <Button className="text-white bg-teal-500" onClick={handleCreateOrUpdate}>
                                {editingId !== null ? "Update" : "Create"}
                            </Button>
                        </div>

                        <Table>
                            <TableHeader>
                                <TableRow>
                                    <TableHead><Checkbox className="border-black" /></TableHead>
                                    <TableHead>ID</TableHead>
                                    <TableHead>Name</TableHead>
                                    <TableHead>Status</TableHead>
                                    <TableHead>Created by</TableHead>
                                    <TableHead>Updated by</TableHead>
                                    <TableHead>Actions</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {userCatalogue.length > 0 ? (
                                    userCatalogue.map((catalogue) => (
                                        <TableRow key={catalogue.id}>
                                            <TableCell><Checkbox className="border-gray-400"/></TableCell>
                                            <TableCell>{catalogue.id}</TableCell>
                                            <TableCell>{catalogue.name}</TableCell>
                                            <TableCell>
                                                {catalogue.publish === 0 ? "Inactive" : catalogue.publish === 1 ? "Active" : "Archived"}
                                            </TableCell>
                                            <TableCell>{catalogue.addedBy}</TableCell>
                                            <TableCell>{catalogue.editedBy}</TableCell>
                                            <TableCell>
                                                <Button variant="outline" size="sm" onClick={() => handleEdit(catalogue)}>Edit</Button>
                                                <Button 
                                                    variant="outline" 
                                                    size="sm" 
                                                    className="text-red-500 border-red-500 ml-2"
                                                    onClick={() => handleDelete(catalogue.id)}
                                                >
                                                    Delete
                                                </Button>
                                            </TableCell>
                                        </TableRow>
                                    ))
                                ) : (
                                    <TableRow>
                                        <TableCell colSpan={7} className="text-center">
                                            No data
                                        </TableCell>
                                    </TableRow>
                                )}
                            </TableBody>
                        </Table>
                    </CardContent>
                    <CardFooter>
                        <p>User catalogue management</p>
                    </CardFooter>
                </Card>
            </div>
        </>
    );
};

export { Catalogue };