export interface CreateCustomerRequest  {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  birthDate: string;
  password: string;
  address: {
    street: string;
    city: string;
    postalCode: string;
    country: string;
  };
}

export interface CustomerResponse {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  customerId: string;
}