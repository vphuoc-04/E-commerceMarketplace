import { useEffect, useState } from "react";
import { getAllPermissions, createPermission, updatePermission, deletePermission, Permission } from "@/services/PermissionService";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardContent, CardFooter } from "@/components/ui/card";
import { Table, TableHeader, TableRow, TableHead, TableBody, TableCell } from "@/components/ui/table";
import PageHeading from "@/components/admins/heading";

const PermissionView = () => {
    const [permissions, setPermissions] = useState<Permission[]>([]);
    const [name, setName] = useState("");
    const [publish, setPublish] = useState(1);
    const [description, setDescription] = useState("");
    const [editingId, setEditingId] = useState<number | null>(null);

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        const data = await getAllPermissions();
        setPermissions(data);
    };

    const handleCreateOrUpdate = async () => {
        if (!name.trim()) return;
        let success = false;
        if (editingId !== null) {
            success = await updatePermission(editingId, name, publish, description);
            setEditingId(null);
        } else {
            success = await createPermission(name, publish, description);
        }
        if (success) {
            setName("");
            setPublish(1);
            setDescription("");
            fetchData();
        }
    };

    const handleEdit = (permission: Permission) => {
        setEditingId(permission.id);
        setName(permission.name);
        setPublish(permission.publish);
        setDescription(permission.description || "");
    };

    const handleDelete = async (id: number) => {
        const success = await deletePermission(id);
        if (success) {
            setPermissions(permissions.filter(p => p.id !== id));
        }
    };

    const breadcrumb = {
        title: "Quản lí quyền hạn",
        route: "/admin/permission/index"
    }

    return (
        <>
            <PageHeading breadcrumb={breadcrumb} />
            <div className="container p-3">
                <Card>
                    <CardContent>
                        <div className="flex gap-2 mb-4">
                            <Input value={name} onChange={e => setName(e.target.value)} placeholder="Permission name..." />
                            <Input value={description} onChange={e => setDescription(e.target.value)} placeholder="Description..." />
                            <select value={publish} onChange={e => setPublish(Number(e.target.value))} className="border rounded px-2">
                                <option value={1}>Active</option>
                                <option value={0}>Inactive</option>
                                <option value={2}>Archived</option>
                            </select>
                            <Button className="text-white bg-teal-500" onClick={handleCreateOrUpdate}>
                                {editingId !== null ? "Update" : "Create"}
                            </Button>
                        </div>
                        <Table>
                            <TableHeader>
                                <TableRow>
                                    <TableHead>ID</TableHead>
                                    <TableHead>Name</TableHead>
                                    <TableHead>Description</TableHead>
                                    <TableHead>Status</TableHead>
                                    <TableHead>Actions</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {permissions.length > 0 ? permissions.map(permission => (
                                    <TableRow key={permission.id}>
                                        <TableCell>{permission.id}</TableCell>
                                        <TableCell>{permission.name}</TableCell>
                                        <TableCell>{permission.description}</TableCell>
                                        <TableCell>{permission.publish === 1 ? "Active" : permission.publish === 0 ? "Inactive" : "Archived"}</TableCell>
                                        <TableCell>
                                            <Button variant="outline" size="sm" onClick={() => handleEdit(permission)}>Edit</Button>
                                            <Button variant="outline" size="sm" className="text-red-500 border-red-500 ml-2" onClick={() => handleDelete(permission.id)}>Delete</Button>
                                        </TableCell>
                                    </TableRow>
                                )) : (
                                    <TableRow>
                                        <TableCell colSpan={5} className="text-center">No data</TableCell>
                                    </TableRow>
                                )}
                            </TableBody>
                        </Table>
                    </CardContent>
                    <CardFooter>
                        <p>Permission management</p>
                    </CardFooter>
                </Card>
            </div>
        </>
    );
};

export default PermissionView; 