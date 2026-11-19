import axios from "axios";
import { CreateCustomerRequest } from "../model/type";

export const api = axios.create({
    baseURL: "/api",
});

export function createCustomer(data: CreateCustomerRequest) {
    console.log("NEW createCustomer function");

    return api.post("/customers", data);
}