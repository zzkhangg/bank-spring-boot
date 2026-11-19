import { useState } from "react";
import { useForm } from "react-hook-form";

import { createCustomer } from "@/features/customers/api/customers";
import type { CreateCustomerRequest } from "@/features/customers/model/type";

export const RegisterPage = () => {
  const [message, setMessage] = useState("");

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<CreateCustomerRequest>();

  function onSubmit(data: CreateCustomerRequest) {
    setMessage("");

    return createCustomer(data)
      .then(() => {
        setMessage("Customer created successfully!");
      })
      .catch(err => {
        console.error(err);
        setMessage("Failed to create customer");
      });
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <h1>Register</h1>

      <input
        placeholder="First name"
        {...register("firstName", {
          required: "First name is required",
        })}
      />
      <p>{errors.firstName?.message}</p>

      <input
        placeholder="Last name"
        {...register("lastName", {
          required: "Last name is required",
        })}
      />
      <p>{errors.lastName?.message}</p>

      <input
        type="email"
        placeholder="Email"
        {...register("email", {
          required: "Email is required",
        })}
      />
      <p>{errors.email?.message}</p>

      <input
        type="password"
        placeholder="Password"
        {...register("password", {
          required: "Password is required",
          minLength: {
            value: 8,
            message: "Password must contain at least 8 characters",
          },
        })}
      />
      <p>{errors.password?.message}</p>

      <input
        placeholder="Phone number"
        {...register("phoneNumber", {
          required: "Phone number is required",
        })}
      />

      <input
        type="date"
        {...register("birthDate", {
          required: "Birth date is required",
        })}
      />

      <input
        placeholder="Street"
        {...register("address.street", {
          required: "Street is required",
        })}
      />

      <input
        placeholder="City"
        {...register("address.city", {
          required: "City is required",
        })}
      />

      <input
        placeholder="Postal code"
        {...register("address.postalCode", {
          required: "Postal code is required",
        })}
      />

      <input
        placeholder="Country"
        {...register("address.country", {
          required: "Country is required",
        })}
      />

      <button type="submit" disabled={isSubmitting}>
        {isSubmitting ? "Creating..." : "Register"}
      </button>

      <p role="status">{message}</p>
    </form>
  );
};