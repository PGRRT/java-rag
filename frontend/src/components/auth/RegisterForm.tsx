import ContentWrapper from "@/components/ui/ContentWrapper";
import { Button } from "@mantine/core";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { registerSchema } from "@/api/schemas/registerSchema";
import type { RegisterFormData } from "@/api/schemas/registerSchema";
import type { RegisterData } from "@/types/user";
import { css, cx } from "@emotion/css";
import { breakPointsMediaQueries } from "@/constants/breakPoints";
import { useState } from "react";
import RegisterView from "@/components/formView/RegisterView";
import VerifyEmail from "@/components/formView/VerifyEmail";
import { showAsyncToastAndRedirect, showToast } from "@/utils/showToast";
import { useNavigate } from "react-router-dom";
import { userApi } from "@/api/userApi";
import Link from "@/components/ui/LinkRenderer";
import { useAuth } from "@/hooks/useAuth";
import AgreeFooter from "@/components/AgreeFooter";
import loginBg from "@/assets/login-bg.png";

const RegisterForm = () => {
  const { register: registerUser, createOtp, clearAuthError, user } = useAuth();
  const navigate = useNavigate();
  // const [step, setStep] = useState<number>(1);
  const [step, setStep] = useState<number>(0);
  const [verifyEmailLoading, setVerifyEmailLoading] = useState<boolean>(false);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    getValues,
    setError,
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

  const onSubmit = async (formData: RegisterFormData) => {
    const userData: RegisterData = {
      email: formData.email,
      password: formData.password,
      confirmPassword: formData.confirmPassword,
      otp: formData?.otp ?? "",
    };

    clearAuthError();

    const { data, error } = await registerUser(userData);

    if (error) {
      console.log("error on submit", error);
      setError("otp", { type: "manual", message: error });

      return;
    }


    // this is used to make isSubmitting true after the user is registered (so that the button is disabled while redirecting)
    await showAsyncToastAndRedirect(
      "Account created successfully! Redirecting to homepage...",
      "/",
      2000,
      navigate
    );
  };

  const createEmailVerificationPassword = async (data: RegisterFormData) => {
    console.log("createEmailVerificationPassword");

    setVerifyEmailLoading(true);

    try {
      const userEmail = getValues("email");

      const { data, error } = await createOtp(userEmail);

      if (error) {
        return;
      }

      setStep(step + 1);
    } catch (error) {
      console.error("Error creating email verification password:", error);
    }
    setVerifyEmailLoading(false);
  };

  return (
   
      <ContentWrapper
        width="100%"
        padding="20px"
        height="100%"
        direction="column"
        justify="space-between"
        align="center"
      >
        <div /> {/* Spacer */}
        <ContentWrapper
          width="100%"
          maxWidth="450px"
          direction="column"
          gap="30px"
        >
          {step === 0 ? (
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
                <Button type="submit" disabled={verifyEmailLoading}>
                  Verify email
                </Button>
              </RegisterView>
            </form>
          ) : step === 1 ? (
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
                <Button type="submit" disabled={isSubmitting}>
                  Create your account
                </Button>
              </VerifyEmail>
            </form>
          ) : null}
        </ContentWrapper>
        <AgreeFooter />
      </ContentWrapper>

 
  );
};

export default RegisterForm;
