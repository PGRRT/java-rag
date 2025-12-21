import ContentWrapper from "@/components/ui/ContentWrapper";
import IconWrapper from "@/components/ui/IconWrapper";
import colorPalette from "@/constants/colorPalette";
import { styles } from "@/constants/styles";
import { typography } from "@/constants/typography";
import { SidebarOption, type SidebarOptionType } from "@/types/sidebarOptions";
import { css, cx } from "@emotion/css";
import { useNavigate } from "react-router-dom";

const SidebarChatItem = ({
  label,
  icon,
  isDisabled = false,
  smallPadding = false,
  type,
  id,
}: {
  label: string;
  icon?: React.ElementType;
  isDisabled?: boolean;
  smallPadding?: boolean;
  type?: SidebarOptionType;
  id?: number;
}) => {
  const navigate = useNavigate();

  const getActionForOption = () => {
    switch (type) {
      case SidebarOption.NewChat:
        return () => {
          navigate("/");
        };
      case SidebarOption.SearchChats:
        return () => {
          // Implement search chats action
          // console.log("Search chats action triggered");
        };
      case SidebarOption.ChatItem:
        return () => {
          navigate(`/c/${id}`);
        };
      default:
        return () => {};
    }
  };

  const action = getActionForOption();
  return (
    <>
      <ContentWrapper
        key={label}
        gap="10px"
        align="center"
        customCss={css`
          transition: background 0.2s;
          padding: ${smallPadding ? "8px" : "10px"} 10px;
          border-radius: ${styles.borderRadius.small};
          width: 100%;
          color: ${colorPalette.text};
          &:hover {
            background: ${colorPalette.backgroundTertiary};
          }

          ${isDisabled &&
          css`
            opacity: 0.5;
            pointer-events: none;
          `}
        `}
        as="button"
        onClick={action}
      >
        {icon && <IconWrapper size={14} Icon={icon} />}

        <span
          className={cx(
            typography.textM,
            css`
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
              max-width: 90%;
            `
          )}
        >
          {label}
        </span>
      </ContentWrapper>
    </>
  );
};

export default SidebarChatItem;
