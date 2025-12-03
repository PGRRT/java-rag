"use client";
import ContentWrapper from "@/components/ui/ContentWrapper";
import Input from "@/components/ui/Input";
import LinkRenderer from "@/components/ui/LinkRenderer";
import { Button } from "@/components/ui/shadcn/button";
import { typography } from "@/constants/typography";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { loginSchema, LoginFormData } from "@/lib/schemas/loginSchema";
import { userApi } from "@/lib/api/userApi";
import { Credentials, User } from "@/types/user";
import { css } from "@emotion/react";
import { breakPointsMediaQueries } from "@/constants/breakPoints";
import { showAsyncToastAndRedirect, showToast } from "@/utils/showToast";
import { useRouter } from "next/navigation";
import { useAuth } from "@/hooks/useAuth";
import { useEffect } from "react";

const LoginForm = () => {
  const router = useRouter();
  const { login: loginUser, clearAuthError, user, isLoading } = useAuth();


  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormData) => {
    const userData: Credentials = {
      email: data.email,
      password: data.password,
    };

    try {
      clearAuthError();
      console.log("userData", userData);
      await loginUser(userData);
      console.log("User logged in successfully:", data);

      // this is used to make isSubmitting true after the user is logged in (so that the button is disabled while redirecting)
      await showAsyncToastAndRedirect(
        "Login successful! Redirecting to homepage...",
        "/",
        2000,
        router
      );
    } catch (error) {
      console.error("Error logging in user:", error);
      showToast.error(
        error?.response?.data?.message || "Error logging in user"
      );
    }
  };

  return (
    <ContentWrapper
      align="center"
      direction="column"
      width="100%"
      customCss={css`
        ${breakPointsMediaQueries.desktop} {
          margin: 150px 0 0 0;
        }
      `}
    >
      <ContentWrapper
        maxWidth="600px"
        width="100%"
        padding="20px"
        direction="column"
        gap="25px"
      >
        <h4 className={typography.textXxl}>Log in to your account</h4>
        <ContentWrapper gap="10px">
          <span className={typography.textL}>Don't have an account?</span>

          <LinkRenderer href="/register" includeLinkStyles>
            Sign up
          </LinkRenderer>
        </ContentWrapper>
        <form onSubmit={handleSubmit(onSubmit)}>
          <ContentWrapper direction="column" gap="25px">
            <Input
              label="Email"
              type="email"
              {...register("email")}
              error={errors.email?.message}
            />

            <Input
              label="Password"
              type="password"
              {...register("password")}
              error={errors.password?.message}
            />

            <Button type="submit" size="lg" disabled={isSubmitting}>
              Log In
            </Button>
          </ContentWrapper>
        </form>
      </ContentWrapper>
    </ContentWrapper>
  );
};

export default LoginForm;
