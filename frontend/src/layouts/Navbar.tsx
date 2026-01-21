import { Avatar } from "@/components/ui/Avatar";
import ContentWrapper from "@/components/ui/ContentWrapper";
import SelectInput from "@/components/ui/SelectInput";
import colorPalette from "@/constants/colorPalette";
import { styles } from "@/constants/styles";
import { useAuth } from "@/hooks/useAuth";
import useViewport from "@/hooks/useViewport";
import { css } from "@emotion/css";
import { Button } from "@mantine/core";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

export const navbarHeight = 66;

const Navbar = () => {
  const { t } = useTranslation();
  const ragOptions = [
    {
      label: t("navbar.ragOptions.bielik"),
      value: "bielik",
    },
    {
      label: t("navbar.ragOptions.classical_rag"),
      value: "classical_rag",
    },
  ];

  const [rag, setRag] = useState(ragOptions[0].value);
  const { isMobile } = useViewport();
  const margin = isMobile ? "0 0 0 60px" : "0";
  const { user } = useAuth();

  return (
    <>
      <ContentWrapper
        justify="space-between"
        margin={margin}
        gap="1rem"
        align="center"
        padding={`0 ${styles.padding.small}`}
        customCss={css`
          min-height: ${navbarHeight}px;
          height: ${navbarHeight}px;
          max-height: ${navbarHeight}px;

          // border-bottom: 1px solid ${colorPalette.strokePrimary};

          background-color: ${colorPalette.background};
          position: sticky;
          top: 0;
          z-index: 10;
        `}
      >
        <SelectInput value={rag} onChange={setRag} options={ragOptions} />

        <ContentWrapper direction="row" gap="10px">
          {user ? (
            <Avatar email={user.email} size={36} />
          ) : (
            <>
              <Button component={Link} to="/sign-in">
                {t("auth.signIn")}
              </Button>
              {isMobile ? null : (
                <Button variant="outline" component={Link} to="/sign-up">
                  {t("auth.signUp")}
                </Button>
              )}
            </>
          )}
        </ContentWrapper>
      </ContentWrapper>
    </>
  );
};

export default Navbar;
