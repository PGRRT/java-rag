import RegisterForm from "@/components/auth/RegisterForm";
import ContentWrapper from "@/components/ui/ContentWrapper";
import { breakPointsMediaQueries } from "@/constants/breakPoints";
import { css, cx } from "@emotion/css";
import loginBg from "@/assets/login-bg.png";
import Logo from "@/components/ui/Logo";
import { useNavigate } from "react-router-dom";

const SignUpPage = () => {
  const navigate = useNavigate();
  const navigateToHome = () => {
    navigate("/");
  };

  return (
    <ContentWrapper
      align="center"
      direction="row"
      width="100%"
      customCss={css`
        height: 100vh;
      `}
    >
      <ContentWrapper
        customCss={css`
          position: absolute;
          top: 20px;
          left: 20px;
        `}
      >
        <Logo size={44} onClick={navigateToHome} />
      </ContentWrapper>

      <RegisterForm />

      <ContentWrapper
        width="100%"
        height="100%"
        customCss={cx(
          "",
          css`
            display: none;
            height: 100vh;
            overflow: hidden;
            ${breakPointsMediaQueries.desktop} {
              display: block;
            }
          `
        )}
      >
        <img
          src={loginBg}
          alt="Login illustration"
          className={css`
            height: 100vh;
            max-height: inherit;
            width: 100%;
            object-fit: cover;
          `}
        />
      </ContentWrapper>
    </ContentWrapper>
  );
};

export default SignUpPage;
