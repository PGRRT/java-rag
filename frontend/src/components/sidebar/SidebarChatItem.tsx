import ContentWrapper from "@/components/ui/ContentWrapper";
import IconWrapper from "@/components/ui/IconWrapper";
import colorPalette from "@/constants/colorPalette";
import { styles } from "@/constants/styles";
import { typography } from "@/constants/typography";
import { SidebarOption, type SidebarOptionType } from "@/types/sidebarOptions";
import { css, cx } from "@emotion/css";
import { useNavigate } from "react-router-dom";
import { MoreVertical, Trash2 } from "lucide-react";
import { useState } from "react";
import CustomPopover from "@/components/ui/CustomPopover";
import { showToast } from "@/utils/showToast";
import { chatApi } from "@/api/chatApi";
import type { UUID } from "@/types/global";
import { useAppDispatch } from "@/redux/hooks";
import { deleteChatAction } from "@/redux/slices/chatSlice";
import { useTranslation } from "react-i18next";

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
  id?: UUID;
}) => {
  const navigate = useNavigate();
  const [popoverOpen, setPopoverOpen] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const dispatch = useAppDispatch();
  const { t } = useTranslation();

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

  const handleDelete = async (e: React.MouseEvent) => {
    e.stopPropagation();

    if (!id) return;

    setIsDeleting(true);

    const res = await dispatch(deleteChatAction(id));

    if (deleteChatAction.rejected.match(res)) {
      // toast is shown in the thunk
      console.error("Error creating chat:", res.payload);
    }
    setIsDeleting(false);
    setPopoverOpen(false);
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
          position: relative;
          &:hover {
            background: ${colorPalette.backgroundTertiary};
          }

          &:hover .chat-menu {
            opacity: 1;
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
              flex: 1;
              text-align: left;
            `,
          )}
        >
          {label}
        </span>

        {type === SidebarOption.ChatItem && (
          <CustomPopover
            // width={240}
            position="right"
            open={popoverOpen}
            setOpen={setPopoverOpen}
            trigger={
              <div
                className={cx(
                  "chat-menu",
                  css`
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    padding: 4px;
                    border-radius: ${styles.borderRadius.small};
                    opacity: 0;
                    transition: all 0.2s;
                    &:hover {
                      background: ${colorPalette.strokePrimary};
                    }
                  `,
                )}
                onClickCapture={(e) => {
                  e.stopPropagation();
                  setPopoverOpen(!popoverOpen);
                }}
              >
                <IconWrapper size={14} Icon={MoreVertical} />
              </div>
            }
            content={
              <ContentWrapper direction="column" gap="0px">
                <button
                  onClick={handleDelete}
                  disabled={isDeleting}
                  className={css`
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    padding: 8px 12px;
                    width: 100%;
                    background: none;
                    border: none;
                    color: ${colorPalette.text};
                    cursor: pointer;
                    border-radius: ${styles.borderRadius.small};
                    transition: background 0.2s;
                    &:hover {
                      background: ${colorPalette.backgroundTertiary};
                    }
                    &:disabled {
                      opacity: 0.5;
                      cursor: not-allowed;
                    }
                  `}
                >
                  <IconWrapper size={14} Icon={Trash2} />
                  <span className={typography.textM}>
                    {isDeleting ? t("chat.deleting") : t("chat.delete")}
                  </span>
                </button>
              </ContentWrapper>
            }
          />
        )}
      </ContentWrapper>
    </>
  );
};

export default SidebarChatItem;
