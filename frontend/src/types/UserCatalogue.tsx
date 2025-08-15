export interface UserCatalogue {
    id: number,
    name: string,
    publish: number,
    addedBy: number | null,
    editedBy: number | null
}