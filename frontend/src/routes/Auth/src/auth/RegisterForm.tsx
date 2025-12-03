"use client";
import ContentWrapper from "@/components/ui/ContentWrapper";
import Input from "@/components/ui/Input";
import LinkRenderer from "@/components/ui/LinkRenderer";
import { Button } from "@/components/ui/shadcn/button";
import { typography } from "@/constants/typography";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { RegisterFormData, registerSchema } from "@/lib/schemas/registerSchema";
import { userApi } from "@/lib/api/userApi";
import { RegisterData, User } from "@/types/user";
import { css } from "@emotion/react";
import { breakPointsMediaQueries } from "@/constants/breakPoints";
import { useEffect, useState } from "react";
import RegisterView from "@/components/formView/RegisterView";
import VerifyEmail from "@/components/formView/VerifyEmail";
import { showAsyncToastAndRedirect, showToast } from "@/utils/showToast";
import { useRouter } from "next/navigation";
import {} from "next/navigation";
import { useAuth } from "@/hooks/useAuth";
const RegisterForm = () => {
  const { register: registerUser, clearAuthError, user } = useAuth();
  const router = useRouter();
  const [step, setStep] = useState<number>(0);
  const [verifyEmailLoading, setVerifyEmailLoading] = useState<boolean>(false);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    getValues,
    trigger,
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      email: "",
      password: "",
      confirmPassword: "",
      otp: "",
    },
  });

  const onSubmit = async (data: RegisterFormData) => {
    const userData: RegisterData = {
      email: data.email,
      password: data.password,
      confirmPassword: data.confirmPassword,
      otp: data?.otp ?? "",
    };

    try {
      clearAuthError();
      console.log("userData", userData);

      await showToast.async.withLoading(() => registerUser(userData), {
        loadingMessage: "Creating your account...",
      });

      // this is used to make isSubmitting true after the user is registered (so that the button is disabled while redirecting)
      await showAsyncToastAndRedirect(
        "Account created successfully! Redirecting to homepage...",
        "/",
        2000,
        router
      );
    } catch (error) {
      console.error("Error registering user:", error);
      showToast.error(error?.response?.data?.message || "Registration failed");
    }
  };

  const createEmailVerificationPassword = async (data: RegisterFormData) => {
    console.log("createEmailVerificationPassword");

    setVerifyEmailLoading(true);

    try {
      const userEmail = getValues("email");

      await showToast.async.withLoading(
        () => userApi.createEmailVerificationPassword(userEmail),
        {
          loadingMessage: "Sending verification email...",
          successMessage: `Verification email sent to ${userEmail}!`,
          errorMessage: "Failed to send verification email",
        }
      );

      setStep(step + 1);
    } catch (error) {
      console.error("Error creating email verification password:", error);
    }
    setVerifyEmailLoading(false);
  };

  console.log("errors", errors);

  return (
    <ContentWrapper
      align="center"
      direction="column"
      width="100%"
      customCss={css`
        ${breakPointsMediaQueries.desktop} {
          margin: 50px 0 0 0;
        }
      `}
    >
      <ContentWrapper
        maxWidth="600px"
        padding="20px"
        width="100%"
        direction="column"
        gap="25px"
      >
        {step == 0 ? (
          <form
            onSubmit={handleSubmit(createEmailVerificationPassword)}
            className={css`
              width: inherit;
              display: inherit;
              flex-direction: inherit;
              gap: inherit;
            `}
          >
            <RegisterView register={register} errors={errors}>
              <Button size="lg" type="submit" disabled={verifyEmailLoading}>
                Verify email
              </Button>
            </RegisterView>
          </form>
        ) : step == 1 ? (
          <form
            onSubmit={handleSubmit(onSubmit)}
            className={css`
              width: inherit;
              display: inherit;
              flex-direction: inherit;
              gap: inherit;
            `}
          >
            <VerifyEmail
              register={register}
              errors={errors}
              email={getValues("email")}
            >
              <Button type="submit" size="lg" disabled={isSubmitting}>
                Create your Signaro account
              </Button>
            </VerifyEmail>
          </form>
        ) : null}
        <span className={typography.textS}>
          By creating an account, you agree to Signaro's{" "}
          <LinkRenderer href="/conditions" includeLinkStyles target="_blank">
            Conditions of Use
          </LinkRenderer>{" "}
          and{" "}
          <LinkRenderer href="/privacy" includeLinkStyles target="_blank">
            Privacy Notice
          </LinkRenderer>
          .
        </span>
      </ContentWrapper>
    </ContentWrapper>
  );
};

export default RegisterForm;
