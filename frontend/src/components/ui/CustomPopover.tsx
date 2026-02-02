import colorPalette from "@/constants/colorPalette";
import { styles } from "@/constants/styles";
import { Popover } from "@mantine/core";

const CustomPopover = ({
  trigger,
  content,
  open,
  setOpen,
  width,
  position,
}: {
  trigger: React.ReactNode;
  content: React.ReactNode;
  open?: boolean;
  setOpen?: (open: boolean) => void;
  width?: number | string;
  position?:
    | "top"
    | "bottom"
    | "left"
    | "right"
    | "top-start"
    | "top-end"
    | "bottom-start"
    | "bottom-end"
    | "left-start"
    | "left-end"
    | "right-start"
    | "right-end";
}) => {
  const popoverProps =
    open !== undefined && setOpen !== undefined
      ? { opened: open, onChange: setOpen }
      : {};

  return (
    <>
      <Popover
        {...popoverProps}
        width={width ?? 200}
        position={position ?? "bottom-start"}
  //       withinPortal
  //       middlewares={{ 
  //   flip: false, // Blokuje przeskakiwanie na dół/górę
  //   shift: true, // Pozwala na korektę góra/dół, żeby nie wystawało poza ekran
  //   inline: true 
  // }}
        // middlewares={{ flip: false, shift: true }}
        styles={{
          dropdown: {
            borderRadius: styles.borderRadius.small,
            background: colorPalette.backgroundBright,
            border: `1px solid ${colorPalette.strokePrimary}`,
          },
        }}
      >
        <Popover.Target>{trigger}</Popover.Target>

        <Popover.Dropdown>{content}</Popover.Dropdown>
      </Popover>
    </>
  );
};

export default CustomPopover;
